package com.ehocam.api_gateway.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Cache configuration for 5N1K API Gateway
 * Defines TTL values and cache manager for different cache types
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // TTL Configuration - can be overridden via application.yml
    @Value("${cache.ttl.todayByCountry:600}") // 10 minutes
    private long todayByCountryTtl;

    @Value("${cache.ttl.eventDetail:1800}") // 30 minutes
    private long eventDetailTtl;

    @Value("${cache.ttl.similarEvents:3600}") // 60 minutes
    private long similarEventsTtl;

    @Value("${cache.ttl.trending24h:300}") // 5 minutes
    private long trending24hTtl;

    @Value("${cache.ttl.supportedLocales:21600}") // 6 hours
    private long supportedLocalesTtl;

    @Value("${cache.ttl.countries:21600}") // 6 hours
    private long countriesTtl;

    @Value("${cache.ttl.languages:21600}") // 6 hours
    private long languagesTtl;

    /**
     * Cache manager configuration with specific TTL for each cache
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(300)) // Default 5 minutes
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Cache-specific configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // todayByCountry cache - 10 minutes
        cacheConfigurations.put("todayByCountry", 
                defaultConfig.entryTtl(Duration.ofSeconds(todayByCountryTtl)));
        
        // eventDetail cache - 30 minutes
        cacheConfigurations.put("eventDetail", 
                defaultConfig.entryTtl(Duration.ofSeconds(eventDetailTtl)));
        
        // similarEvents cache - 60 minutes
        cacheConfigurations.put("similarEvents", 
                defaultConfig.entryTtl(Duration.ofSeconds(similarEventsTtl)));
        
        // trending24h cache - 5 minutes
        cacheConfigurations.put("trending24h", 
                defaultConfig.entryTtl(Duration.ofSeconds(trending24hTtl)));
        
        // supportedLocales cache - 6 hours
        cacheConfigurations.put("supportedLocales", 
                defaultConfig.entryTtl(Duration.ofSeconds(supportedLocalesTtl)));
        
        // countries cache - 6 hours
        cacheConfigurations.put("countries", 
                defaultConfig.entryTtl(Duration.ofSeconds(countriesTtl)));
        
        // languages cache - 6 hours
        cacheConfigurations.put("languages", 
                defaultConfig.entryTtl(Duration.ofSeconds(languagesTtl)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
