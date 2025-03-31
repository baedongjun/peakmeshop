package com.peakmeshop.domain.service;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictCache(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }

    public void evictCacheForKey(String cacheName, String key) {
        cacheManager.getCache(cacheName).evict(key);
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
        });
    }
}