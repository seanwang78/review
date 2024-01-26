package com.uaepay.gateway.cgs.app.service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * <p>缓存服务对象</p>
 *
 * @author Yadong Lu
 * @version $ Id: CacheServiceImpl, v 1.0 2019/10/11 Ranber Exp $
 */
@Service
public class CacheClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public <T> boolean store(String cacheKey, T cacheValue, Long seconds) {
        try {
            redisTemplate.executePipelined(new SessionCallback<Void>() {
                @Override
                public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                    operations.opsForValue().set((K) cacheKey, (V) cacheValue);
                    operations.expire((K) cacheKey,
                            Duration.ofSeconds(seconds).getSeconds(),
                            TimeUnit.SECONDS);
                    return null;
                }
            });

        } catch (Exception e) {
            logger.error("store cache error.", e);
            return false;
        }
        return true;
    }

    public <T> boolean storeForever(String cacheKey, T cacheValue) {
        try {
            redisTemplate.opsForValue().set(cacheKey, cacheValue);
        } catch (Exception e) {
            logger.error("store cache forever error.", e);
            return false;
        }
        return true;
    }


    public void clearCache(String cacheKey) {
        logger.info("删除key[{}]的缓存 Token 信息。", cacheKey);
        redisTemplate.delete(cacheKey);
    }


    public <T> T getCacheByKey(String cacheKey) {
        return (T) redisTemplate.opsForValue().get(cacheKey);
    }



    public boolean setExpireTime(String cacheKey, Long expireTime) {
        try {
            redisTemplate.executePipelined(new SessionCallback<Void>() {
                @Override
                public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                    operations.expire((K) cacheKey, Duration.ofSeconds(expireTime).getSeconds(), TimeUnit.SECONDS);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("set cache expire time error.", e);
            return false;
        }
        return true;
    }


    public <T> boolean refreshValue(String cacheKey, T cacheValue) {
        //TODO 待优化
        try {
            Long expireTime = redisTemplate.getExpire(cacheKey);
            redisTemplate.executePipelined(new SessionCallback<Void>() {
                @Override
                public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                    operations.opsForValue().set((K) cacheKey, (V) cacheValue);
                    operations.expire((K) cacheKey, Duration.ofSeconds(expireTime).getSeconds(), TimeUnit.SECONDS);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("refresh value error.", e);
            return false;
        }
        return true;
    }


    public Long getCount(String cacheKey) {
        try {
            long count = redisTemplate.boundValueOps(cacheKey).increment(1);
            return count;
        } catch (Exception e) {
            logger.error("getCount error.", e);
            return 0L;
        }
    }


    public Long getCount(String cacheKey, Long exprieTime) {
        try {
            Long count = redisTemplate.boundValueOps(cacheKey).increment();
            if (count == 1) {
                redisTemplate.boundValueOps(cacheKey).expire(exprieTime, TimeUnit.SECONDS);
            }
            return count;
        } catch (Exception e) {
            logger.error("getCount error.", e);
            return 0L;
        }
    }
}
