package com.fds.flex.core.portal.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.util.PortalConstant;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReactiveCacheService {
    
    private static final ConcurrentHashMap<String, PublicKey> _RSAKEY_CACHES = new ConcurrentHashMap<>();

    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Mono<PublicKey> getPublicKeyMono(String sessionId, String token) {
        // Tạo cache key từ sessionId
        String cacheKey = PortalConstant.CACHE_KEY_PUBLIC_KEY + StringPool.DASH + sessionId;
        
        // Kiểm tra cache local
        if (_RSAKEY_CACHES.containsKey(cacheKey)) {
            return Mono.just(_RSAKEY_CACHES.get(cacheKey));
        }
        
        // Kiểm tra Redis nếu đã bật
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        Mono<PublicKey> publicKeyMono;
        
        if (isRedisEnabled) {
            publicKeyMono = reactiveRedisTemplate.opsForValue()
                    .get(cacheKey)
                    .cast(String.class)
                    .flatMap(pubKeyStr -> {
                        if (Validator.isNotNull(pubKeyStr)) {
                            try {
                                PublicKey publicKey = convertToPublicKey(pubKeyStr);
                                // Lưu vào cache local
                                _RSAKEY_CACHES.put(cacheKey, publicKey);
                                return Mono.just(publicKey);
                            } catch (Exception e) {
                                log.error("Error converting cached public key: {}", e.getMessage());
                            }
                        }
                        return fetchPublicKey(cacheKey, token);
                    })
                    .defaultIfEmpty(null)
                    .flatMap(publicKey -> {
                        if (publicKey == null) {
                            return fetchPublicKey(cacheKey, token);
                        }
                        return Mono.just(publicKey);
                    });
        } else {
            publicKeyMono = fetchPublicKey(cacheKey, token);
        }
        
        return publicKeyMono;
    }
    
    private Mono<PublicKey> fetchPublicKey(String cacheKey, String token) {
        // Lấy JWT public key từ server
        String jwksUrl = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_INTEGRATED_JWKS_URI), 
                StringPool.BLANK);
        
        if (Validator.isNull(jwksUrl)) {
            log.error("JWKS URI is not configured");
            return Mono.empty();
        }
        
        return webClientBuilder.build()
                .get()
                .uri(jwksUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jwksJson -> {
                    try {
                        // Parse JWKS response và lấy public key
                        // Đây là một đơn giản hóa, trong thực tế bạn cần phân tích JSON và lấy key tương ứng
                        String pubKeyPEM = extractPublicKeyFromJwks(jwksJson, token);
                        
                        PublicKey publicKey = convertToPublicKey(pubKeyPEM);
                        
                        // Lưu vào cache
                        _RSAKEY_CACHES.put(cacheKey, publicKey);
                        
                        // Lưu vào Redis nếu có thể
                        boolean isRedisEnabled = GetterUtil.get(
                                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                                false);
                        
                        if (isRedisEnabled) {
                            reactiveRedisTemplate.opsForValue()
                                    .set(cacheKey, pubKeyPEM)
                                    .subscribe();
                        }
                        
                        return Mono.just(publicKey);
                    } catch (Exception e) {
                        log.error("Error fetching public key: {}", e.getMessage());
                        return Mono.empty();
                    }
                });
    }
    
    // Phương thức trích xuất public key từ JWKS response
    private String extractPublicKeyFromJwks(String jwksJson, String token) {
        // Giả lập - trong thực tế cần phân tích JSON và tìm key phù hợp
        // Đây chỉ là placeholder, cần triển khai logic thực tế ở đây
        return "MOCK_PUBLIC_KEY_PEM"; 
    }
    
    // Chuyển đổi chuỗi PEM thành PublicKey
    private PublicKey convertToPublicKey(String publicKeyPEM) throws Exception {
        // Giả lập - trong thực tế cần convert đúng format
        // Đây chỉ là placeholder, cần triển khai logic thực tế ở đây
        try {
            // Xóa header và footer PEM
            String publicKeyContent = publicKeyPEM
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            
            // Decode từ Base64
            byte[] encoded = Base64.getDecoder().decode(publicKeyContent);
            
            // Tạo PublicKey
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);
            
        } catch (Exception e) {
            log.error("Error converting public key: {}", e.getMessage());
            throw e;
        }
    }
    
    // Xóa cache
    public Mono<Boolean> clearCache(Collection<String> keys) {
        for (String key : keys) {
            _RSAKEY_CACHES.remove(key);
        }
        
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (isRedisEnabled) {
            return reactiveRedisTemplate.delete(keys.toArray(new String[0]))
                    .map(count -> count > 0);
        }
        
        return Mono.just(true);
    }
} 
