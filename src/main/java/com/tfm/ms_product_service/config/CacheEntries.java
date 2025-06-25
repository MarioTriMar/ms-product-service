package com.tfm.ms_product_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Component
@Slf4j
public class CacheEntries {
    /*
    @Autowired
    private CacheManager cacheManager;

    public Map<Object, Object> getAllEntriesInProductCache(String cacheName) {
        org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);
        if (springCache == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cache doesn't exist");
        }
        if (springCache instanceof CaffeineCache caffeineCache) {
            Cache<Object, Object> nativeCache = (Cache<Object, Object>) caffeineCache.getNativeCache();
            return nativeCache.asMap();
        } else {
            throw new IllegalStateException("Cache is not a Caffeine cache");
        }
    }

     */
}
