package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up caches using Caffeine.
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // characterTemplates: This is static data. It should probably never expire, just have a maximum size.
        cacheManager.registerCustomCache("characterTemplate",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .build()
        );

        cacheManager.registerCustomCache("characterTemplates",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .build()
        );

        // userGameState: This is dynamic. It should expire after some periods of inactivity.
        cacheManager.registerCustomCache("userGameState",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterAccess(2, TimeUnit.HOURS)
                        .build()
        );

        cacheManager.registerCustomCache("userDetails",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterAccess(2, TimeUnit.HOURS)
                        .build()
        );

        return cacheManager;
    }
}