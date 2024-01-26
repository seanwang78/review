package com.uaepay.gateway.cgs.app.service.domainservice.token.impl;

import com.uaepay.basis.beacon.common.util.UuidUtil;
import com.uaepay.gateway.cgs.app.service.common.Base64;
import com.uaepay.gateway.cgs.app.service.common.CgsReturnCode;
import com.uaepay.gateway.cgs.app.service.common.SecureRandoms;
import com.uaepay.gateway.cgs.app.service.constants.CacheNameConstants;
import com.uaepay.gateway.cgs.app.service.domain.AccessInfo;
import com.uaepay.gateway.cgs.app.service.domain.AccessToken;
import com.uaepay.gateway.cgs.app.service.domain.LoginToken;
import com.uaepay.gateway.cgs.app.service.domain.Token;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.common.util.GatewayJsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.uaepay.gateway.common.facade.enums.GatewayReturnCode.SYSTEM_ERROR;

@Slf4j
@Service
public class AccessTokenServiceImpl implements AccessTokenService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String SALT = "salt";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${login.disableForceLogout:false}")
    private boolean disableForceLogout;

    @Value("${login.accessToken.ttl:1d}")
    private Duration accessTokenTtl;

    @Value("${common.salt.ttl:30s}")
    private Duration saltTtl;

    @Value("${token.issuer:test}")
    private String issuer;

    @Override
    public AccessInfo login(LoginToken loginToken) {
        String loginTokenCacheKey = CacheNameConstants.LOGIN_TOKEN + loginToken.getKeyInfo().buildKey();
        String origAccessToken = (String)redisTemplate.opsForValue().get(loginTokenCacheKey);
        String deleteAccessToken = null;

        if (StringUtils.isNotBlank(origAccessToken) && !disableForceLogout) {
            List<String> origList = new ArrayList<>();
            origList.add(CacheNameConstants.ACCESS_TOKEN + origAccessToken);
            Long count = redisTemplate.countExistingKeys(origList);
            if (count != null && count == 1) {
                logger.info("准备强制登出: {}", origAccessToken);
                deleteAccessToken = origAccessToken;
            }
        }

        String accessToken = UuidUtil.generate();
        String accessKey = UuidUtil.generate();
        AccessToken tokenInfo = new AccessToken(loginToken.getAccessMember(), accessToken, accessKey);
        redisTemplate.executePipelined(
            new SaveAccessTokenCallback(loginTokenCacheKey, accessToken, tokenInfo, deleteAccessToken));
        return new AccessInfo(accessToken, accessKey);
    }

    @Override
    public AccessToken getAccessToken(String accessToken) {
        return (AccessToken)redisTemplate.opsForValue().get(buildAccessTokenCacheKey(accessToken));
    }

    @Override
    public String generateSalt(String accessToken) {
//        String salt = UuidUtil.generate();
        String salt = Base64.toBase64String(SecureRandoms.getInstance().genBytes(16));
        redisTemplate.executePipelined(new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                K cacheKey = (K)buildSaltCacheKey(accessToken);
                operations.opsForValue().set(cacheKey, (V)salt);
                operations.expire(cacheKey, saltTtl.toMillis(), TimeUnit.MILLISECONDS);
                return null;
            }
        });
        return salt;
    }

    @Override
    public String useSalt(String accessToken) {
        String cacheKey = buildSaltCacheKey(accessToken);
        String salt = (String)redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(salt)) {
            redisTemplate.delete(cacheKey);
        }
        return salt;
    }

    /**
     * 解析base64 为token
     * @param token
     * @return
     */
    public String analysisToken(String token) {

        if (StringUtils.isNotBlank(token)) {
            // 新token解析
            Token realToken = parseToken(token);
            if (!issuer.equals(realToken.getIss())) {
                // token 发行方错误
                throw new GatewayFailException(CgsReturnCode.TOKEN_ISSUER_ERROR);
            }
            return realToken.getTk();
        }
        return null;
    }


    public class SaveAccessTokenCallback implements SessionCallback<Void> {

        public SaveAccessTokenCallback(String loginTokenCacheKey, String accessToken, AccessToken tokenInfo,
            String deleteAccessToken) {
            this.loginTokenCacheKey = loginTokenCacheKey;
            this.accessToken = accessToken;
            this.tokenInfo = tokenInfo;
            this.deleteAccessToken = deleteAccessToken;
        }

        String loginTokenCacheKey;
        String accessToken;
        AccessToken tokenInfo;
        String deleteAccessToken;

        @Override
        public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
            K accessTokenCacheKey = (K)buildAccessTokenCacheKey(accessToken);
            operations.opsForValue().set((K)loginTokenCacheKey, (V)accessToken);
            operations.opsForValue().set(accessTokenCacheKey, (V)tokenInfo);
            operations.expire((K)loginTokenCacheKey, accessTokenTtl.toMillis(), TimeUnit.MILLISECONDS);
            operations.expire(accessTokenCacheKey, accessTokenTtl.toMillis(), TimeUnit.MILLISECONDS);
            if (deleteAccessToken != null) {
                operations.delete((K)buildAccessTokenCacheKey(deleteAccessToken));
            }
            return null;
        }
    }

    private String buildAccessTokenCacheKey(String accessToken) {
        return CacheNameConstants.ACCESS_TOKEN + accessToken;
    }

    private String buildSaltCacheKey(String accessToken) {
        return CacheNameConstants.SALT + accessToken;
    }

    private Token parseToken(String reqToken) {
        try {
            return GatewayJsonUtil.parseObject(new String(org.apache.commons.codec.binary.Base64.decodeBase64(reqToken)), Token.class);
        } catch (IOException e) {
            log.error("[Cgs -> token -> exception]", e);
            throw new GatewayFailException(SYSTEM_ERROR);
        }
    }

}
