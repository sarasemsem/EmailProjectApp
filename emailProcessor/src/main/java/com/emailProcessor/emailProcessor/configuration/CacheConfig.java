package com.emailProcessor.emailProcessor.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig())
                .withCacheConfiguration("emails", redisCacheConfiguration()) // Cache specific configuration
                .withCacheConfiguration("sender", redisCacheConfiguration())
                .withCacheConfiguration("workers", redisCacheConfiguration())
                .withCacheConfiguration("keyword", redisCacheConfiguration())
                .withCacheConfiguration("categories", redisCacheConfiguration())
                .build();
    }

    // Default cache configuration
    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    // Custom cache configuration for 'emails' cache
    private RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Set TTL for cache entries
                .disableCachingNullValues();
    }
}