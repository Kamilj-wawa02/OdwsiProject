package com.example.pw_odwsi_project.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public Cache<String, Integer> unsuccessfulLoginAttemptsCache() {
        return Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();
    }

    @Bean
    public Cache<String, String> lastLoggedIps() {
        return Caffeine.newBuilder().build();
    }

}
