package com.uaepay.gateway.cgs.app.service.domainservice.token;

import com.uaepay.gateway.cgs.app.service.domain.AccessInfo;
import com.uaepay.gateway.cgs.app.service.domain.AccessToken;
import com.uaepay.gateway.cgs.app.service.domain.LoginToken;

/**
 * 登录令牌服务
 * 
 * @author zc
 */
public interface AccessTokenService {

    /**
     * 生成访问令牌，如果已存在登录信息，强制失效
     * 
     * @param loginToken
     *            登录令牌信息
     * @return 访问信息
     */
    AccessInfo login(LoginToken loginToken);

    /**
     * 获取访问令牌信息
     * 
     * @param accessToken
     *            访问令牌
     * @return 访问信息，如果不存在，返回null
     */
    AccessToken getAccessToken(String accessToken);

    /**
     * 生成盐
     * 
     * @param accessToken
     *            访问令牌
     * @return 盐，如果令牌不存在返回null
     */
    String generateSalt(String accessToken);

    /**
     * 使用盐，
     * 
     * @param accessToken
     *            访问令牌
     * @return 盐，如果令牌或盐不存在返回null
     */
    String useSalt(String accessToken);

    /**
     * 解析base64 为token
     * @param token
     * @return
     */
    String analysisToken(String token);

}
