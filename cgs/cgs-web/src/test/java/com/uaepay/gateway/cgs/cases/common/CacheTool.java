package com.uaepay.gateway.cgs.cases.common;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CacheTool {

    @Resource(name = "cacheManager")
    private CacheManager redisCacheManager;

    @Resource(name = "caffeineCacheManager")
    private CacheManager caffeineCacheManager;

    /**
     * 清理所有缓存
     */
    public void evictAll() {
        System.out.println("清理缓存，开始");
        redisCacheManager.getCacheNames().stream().forEach(cacheName -> {
            System.out.printf("清理redis缓存: %s\n", cacheName);
            redisCacheManager.getCache(cacheName).clear();
        });
        caffeineCacheManager.getCacheNames().stream().forEach(cacheName -> {
            System.out.printf("清理caffeine缓存: %s\n", cacheName);
            caffeineCacheManager.getCache(cacheName).clear();
        });
    }

    /**
     * 清理指定缓存
     */
    public void evictAll(String... cacheNames) {
        System.out.println("清理缓存，开始");
        for (String cacheName : cacheNames) {
            System.out.printf("清理缓存: %s\n", cacheName);
            redisCacheManager.getCache(cacheName).clear();
            caffeineCacheManager.getCache(cacheName).clear();
        }
    }

}