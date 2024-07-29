package com.piter.match.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

  @Bean
  public Caffeine<Object, Object> caffeine(@Value("${cache.expiry-in-hours}") Integer expiryTimeInHours) {
    return Caffeine.newBuilder()
        .expireAfterWrite(expiryTimeInHours, TimeUnit.HOURS);
  }

  @Bean
  public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
    CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(caffeine);
    caffeineCacheManager.setAllowNullValues(false);
    caffeineCacheManager.setAsyncCacheMode(true);
    return caffeineCacheManager;
  }
}