package com.uaepay.gateway.cgs.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.stereotype.Service;

import com.uaepay.gateway.cgs.constants.CgsCacheConstants;
import com.uaepay.redis.starter.custom.AbstractRedisCustomizer;

@Service
public class CgsRedisCustomizer extends AbstractRedisCustomizer {

    @Override
    public Map<String, RedisCacheConfiguration> customize() {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put(CgsCacheConstants.ACS_GATEWAY_API_CONFIG, buildCacheConfiguration(Duration.ofHours(1)));
        configMap.put(CgsCacheConstants.DECRYPT_KEY, buildCacheConfiguration(Duration.ofDays(30)));
        return configMap;
    }

}
