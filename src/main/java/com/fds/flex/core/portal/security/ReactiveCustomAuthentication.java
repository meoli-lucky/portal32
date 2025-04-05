package com.fds.flex.core.portal.security;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.common.ultility.string.StringPool;
import com.fds.flex.common.ultility.string.StringUtil;
import com.fds.flex.core.portal.property.PropKey;
import com.fds.flex.core.portal.service.ReactiveCacheService;
import com.fds.flex.core.portal.util.PortalConstant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ReactiveCustomAuthentication {

    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Autowired
    private ReactiveCacheService cacheService;
    
    @Autowired
    private WebClient.Builder webClientBuilder;

    private Mono<String> getTokenFromRedis(String uuid) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (!isRedisEnabled) {
            return Mono.just(StringPool.BLANK);
        }
        
        return reactiveRedisTemplate.opsForValue()
                .get(uuid + StringPool.DASH + PortalConstant.ACCESS_TOKEN)
                .cast(String.class)
                .defaultIfEmpty(StringPool.BLANK);
    }

    private Mono<String> getRefreshTokenFromRedis(String uuid) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (!isRedisEnabled) {
            return Mono.just(StringPool.BLANK);
        }
        
        return reactiveRedisTemplate.opsForValue()
                .get(uuid + StringPool.DASH + PortalConstant.REFRESH_TOKEN)
                .cast(String.class)
                .defaultIfEmpty(StringPool.BLANK);
    }

    private Mono<Long> getTokenExpiredTimeFromRedis(String uuid) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (!isRedisEnabled) {
            return Mono.just(0L);
        }
        
        return reactiveRedisTemplate.opsForValue()
                .get(uuid + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_TIME)
                .cast(Long.class)
                .defaultIfEmpty(0L);
    }

    private Mono<Long> getTokenExpiredInFromRedis(String uuid) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (!isRedisEnabled) {
            return Mono.just(0L);
        }
        
        return reactiveRedisTemplate.opsForValue()
                .get(uuid + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_IN)
                .cast(Long.class)
                .defaultIfEmpty(0L);
    }

    private Mono<String> getTokenProviderNameFromRedis(String uuid) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (!isRedisEnabled) {
            return Mono.just(StringPool.BLANK);
        }
        
        return reactiveRedisTemplate.opsForValue()
                .get(uuid + StringPool.DASH + PortalConstant.TOKEN_PROVIDER_NAME)
                .cast(String.class)
                .defaultIfEmpty(StringPool.BLANK);
    }

    private Mono<JSONObject> getProfile(String token) {
        if (Validator.isNull(token)) {
            return Mono.empty();
        }
        
        String profileUri = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_INTEGRATED_FLEXCORE_PROFILE_URI),
                StringPool.BLANK);
        
        if (Validator.isNull(profileUri)) {
            return Mono.empty();
        }
        
        return webClientBuilder.build()
                .get()
                .uri(profileUri)
                .header(PortalConstant.AUTHORIZATION, PortalConstant.BEARER + StringPool.SPACE + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(JSONObject::new)
                .onErrorResume(e -> {
                    log.error("Error getting profile: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Void> saveToRedis(String sessionId, String token, String refreshToken, String providerType, 
                                  long expiresIn, long expiresAt) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (!isRedisEnabled) {
            return Mono.empty();
        }
        
        Duration expiry = Duration.ofMillis(expiresIn);
        
        return Mono.zip(
                reactiveRedisTemplate.opsForValue()
                        .set(sessionId + StringPool.DASH + PortalConstant.ACCESS_TOKEN, token, expiry),
                reactiveRedisTemplate.opsForValue()
                        .set(sessionId + StringPool.DASH + PortalConstant.REFRESH_TOKEN, refreshToken, expiry),
                reactiveRedisTemplate.opsForValue()
                        .set(sessionId + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_TIME, expiresAt, expiry),
                reactiveRedisTemplate.opsForValue()
                        .set(sessionId + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_IN, expiresIn, expiry),
                reactiveRedisTemplate.opsForValue()
                        .set(sessionId + StringPool.DASH + PortalConstant.TOKEN_PROVIDER_NAME, providerType, expiry)
        ).then();
    }

    private Mono<Authentication> getAuthentication(String username, List<String> roles, String email, 
                                                 String fullname, JSONObject profile) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUsername(username);
        userDetails.setAuthorities(authorities);
        userDetails.setEmail(email);
        userDetails.setFullName(fullname);
        userDetails.setRoles(new HashSet<>(roles));
        userDetails.setProfiles(profile);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        return Mono.just(authentication);
    }

    public Mono<Boolean> isAuthenticated(ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    String sessionId = session.getId();
                    
                    // Kiểm tra token trong session hoặc Redis
                    Mono<String> tokenMono = getTokenFromSession(session, sessionId);
                    
                    return tokenMono
                            .flatMap(token -> {
                                if (Validator.isNull(token)) {
                                    return Mono.just(false);
                                }
                                
                                // Kiểm tra token có hợp lệ không
                                return validateTokenMono(token, sessionId);
                            })
                            .defaultIfEmpty(false);
                });
    }

    private Mono<String> getTokenFromSession(WebSession session, String sessionId) {
        boolean isRedisEnabled = GetterUtil.get(
                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                false);
        
        if (isRedisEnabled) {
            return getTokenFromRedis(sessionId);
        } else {
            Object tokenObj = session.getAttribute(sessionId + StringPool.DASH + PortalConstant.ACCESS_TOKEN);
            return Mono.justOrEmpty(tokenObj != null ? tokenObj.toString() : null)
                    .defaultIfEmpty(StringPool.BLANK);
        }
    }

    private Mono<Boolean> validateTokenMono(String token, String sessionId) {
        if (Validator.isNull(token)) {
            return Mono.just(false);
        }
        
        return cacheService.getPublicKeyMono(sessionId, token)
                .flatMap(publicKey -> {
                    try {
                        Jws<Claims> claimsJws = Jwts.parserBuilder()
                                .setSigningKey(publicKey)
                                .build()
                                .parseClaimsJws(token);
                        
                        Claims claims = claimsJws.getBody();
                        
                        // Kiểm tra thời hạn
                        Instant expiration = claims.getExpiration().toInstant();
                        Instant now = Instant.now();
                        if (expiration.isBefore(now)) {
                            return Mono.just(false);
                        }
                        
                        // Kiểm tra issuer
                        String issuerConfig = GetterUtil.get(
                                PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_INTEGRATED_OAUTH2_ISSUERS), 
                                StringPool.BLANK);
                        List<String> issuers = Arrays.asList(StringUtil.split(issuerConfig));
                        
                        String issuer = claims.getIssuer();
                        if (!issuers.contains(issuer)) {
                            return Mono.just(false);
                        }
                        
                        return Mono.just(true);
                    } catch (Exception e) {
                        log.error("Token validation error: {}", e.getMessage());
                        return Mono.just(false);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Token validation error: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    public boolean hasAnyRole(List<String> roles) {
        // Lấy thông tin từ SecurityContextHolder hiện tại
        Authentication authentication = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .block();
        
        if (authentication == null) {
            return false;
        }
        
        if (roles == null || roles.isEmpty()) {
            return true;
        }
        
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            for (String role : roles) {
                if (userDetails.getRoles().contains(role)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    public Mono<Void> doLogout(ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    String sessionId = session.getId();
                    
                    // Xóa session
                    session.getAttributes().clear();
                    
                    boolean isRedisEnabled = GetterUtil.get(
                            PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                            false);
                    
                    if (isRedisEnabled) {
                        return Mono.when(
                                reactiveRedisTemplate.delete(sessionId + StringPool.DASH + PortalConstant.ACCESS_TOKEN),
                                reactiveRedisTemplate.delete(sessionId + StringPool.DASH + PortalConstant.REFRESH_TOKEN),
                                reactiveRedisTemplate.delete(sessionId + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_IN),
                                reactiveRedisTemplate.delete(sessionId + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_TIME),
                                reactiveRedisTemplate.delete(sessionId + StringPool.DASH + PortalConstant.TOKEN_PROVIDER_NAME)
                        ).then(session.invalidate());
                    } else {
                        return session.invalidate();
                    }
                });
    }

    public Mono<Boolean> doAuth(ServerWebExchange exchange, String token, String refreshToken, 
                               long expiresIn, long expiresAt, String providerType) {
        if (Validator.isNull(token)) {
            return Mono.just(false);
        }
        
        return exchange.getSession()
                .flatMap(session -> {
                    String sessionId = session.getId();
                    
                    return cacheService.getPublicKeyMono(sessionId, token)
                            .flatMap(publicKey -> {
                                try {
                                    Jws<Claims> claimsJws = Jwts.parserBuilder()
                                            .setSigningKey(publicKey)
                                            .build()
                                            .parseClaimsJws(token);
                                    
                                    Claims claims = claimsJws.getBody();
                                    
                                    String fullName = null;
                                    String mail = null;
                                    String username = null;
                                    String clientId = null;
                                    
                                    if (claims.containsKey("name")) {
                                        fullName = (String) claims.get("name");
                                    }
                                    if (claims.containsKey("azp")) {
                                        clientId = (String) claims.get("azp");
                                    }
                                    if (claims.containsKey("email")) {
                                        mail = (String) claims.get("email");
                                    }
                                    if (claims.containsKey("preferred_username")) {
                                        username = (String) claims.get("preferred_username");
                                    }
                                    
                                    List<String> roles = new ArrayList<>();
                                    if (claims.containsKey("resource_access")) {
                                        HashMap<String, Object> ob = (HashMap<String, Object>) claims.get("resource_access");
                                        if (ob.containsKey(clientId)) {
                                            HashMap<String, Object> account = (HashMap<String, Object>) ob.get(clientId);
                                            if (account.containsKey("roles")) {
                                                roles = (List<String>) account.get("roles");
                                            }
                                        }
                                    }
                                    
                                    // Kiểm tra token
                                    String issuerConfig = GetterUtil.get(
                                            PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_INTEGRATED_OAUTH2_ISSUERS), 
                                            StringPool.BLANK);
                                    List<String> issuers = Arrays.asList(StringUtil.split(issuerConfig));
                                    
                                    if (!validateToken(token, claims, issuers)) {
                                        return Mono.just(false);
                                    }
                                    
                                    // Lấy thông tin profile từ server
                                    final String finalUsername = username;
                                    final String finalMail = mail;
                                    final String finalFullName = fullName;
                                    final List<String> finalRoles = roles;
                                    
                                    return getProfile(token)
                                            .flatMap(profile -> {
                                                // Tạo authentication
                                                return getAuthentication(finalUsername, finalRoles, finalMail, finalFullName, profile)
                                                        .flatMap(authentication -> {
                                                            boolean isRedisEnabled = GetterUtil.get(
                                                                    PropKey.getKeyMap().get(PropKey.FLEXCORE_PORTAL_DISTRIBUTED_CACHE_REDIS_ENABLE), 
                                                                    false);
                                                            
                                                            Mono<Authentication> authMono = Mono.just(authentication);
                                                            
                                                            if (isRedisEnabled) {
                                                                return saveToRedis(sessionId, token, refreshToken, providerType, expiresIn, expiresAt)
                                                                        .then(authMono);
                                                            } else {
                                                                // Lưu vào session
                                                                session.getAttributes().put(sessionId + StringPool.DASH + PortalConstant.ACCESS_TOKEN, token);
                                                                session.getAttributes().put(sessionId + StringPool.DASH + PortalConstant.REFRESH_TOKEN, refreshToken);
                                                                session.getAttributes().put(sessionId + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_TIME, expiresAt);
                                                                session.getAttributes().put(sessionId + StringPool.DASH + PortalConstant.TOKEN_EXPIRED_IN, expiresIn);
                                                                session.getAttributes().put(sessionId + StringPool.DASH + PortalConstant.TOKEN_PROVIDER_NAME, providerType);
                                                                
                                                                return authMono;
                                                            }
                                                        })
                                                        .flatMap(authentication -> {
                                                            // Lưu authentication vào SecurityContext
                                                            SecurityContext context = ReactiveSecurityContextHolder.getContext().block();
                                                            if (context != null) {
                                                                context.setAuthentication(authentication);
                                                            }
                                                            return Mono.just(true);
                                                        });
                                            })
                                            .defaultIfEmpty(false);
                                    
                                } catch (Exception e) {
                                    log.error("Authentication error: {}", e.getMessage());
                                    return Mono.just(false);
                                }
                            })
                            .defaultIfEmpty(false);
                });
    }

    private boolean validateToken(String token, Claims claims, List<String> issuercf) {
        try {
            Instant expiration = claims.getExpiration().toInstant();
            Instant now = Instant.now();
            if (expiration.isBefore(now)) {
                return false;
            }
            String issuer = claims.getIssuer();
            if (!issuercf.contains(issuer)) {
                return false;
            }

        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }

        return true;
    }
} 
