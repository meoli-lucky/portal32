package com.fds.flex.core.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fds.flex.common.ultility.GetterUtil;
import com.fds.flex.common.ultility.Validator;
import com.fds.flex.core.portal.property.PropKey;

@Configuration
public class ReactiveRedisConfig {

    @Value("${flexcore.portal.distributed.cache.redis.enable:false}")
    private boolean redisEnabled;

    @Value("${flexcore.portal.distributed.cache.redis.host:localhost}")
    private String redisHost;

    @Value("${flexcore.portal.distributed.cache.redis.port:6379}")
    private String redisPortStr;

    private int getRedisPort() {
        if (Validator.isNull(redisPortStr)) {
            return 6379;
        }
        try {
            return Integer.parseInt(redisPortStr);
        } catch (NumberFormatException e) {
            return 6379;
        }
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        if (!redisEnabled) {
            // Tạo một connection factory giả khi Redis bị vô hiệu hóa
            return new LettuceConnectionFactory();
        }
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, getRedisPort());
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);
        
        RedisSerializationContext<String, Object> context = builder
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();
        
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }
} 
