package com.uaepay.gateway.cgs.domainservice;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.gateway.cgs.domain.AccessToken;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;

/**
 * 访问令牌服务
 * 
 * @author zc
 */
public interface AccessVerifyService {

    /**
     * 验证Token及签名
     * 
     * @param context
     *            请求上下文
     * @throws FailException
     *             访问令牌校验不通过时抛出
     */
    void verifyTokenAndSign(ReceiveOrderContext context) throws FailException;

    /**
     * 验证token
     *
     * @param accessToken
     * @throws FailException
     */
    AccessTokenResponse verifyToken(String accessToken) throws FailException;

    /**
     * 解析access token
     */
    String analysisAccessToken(String accessToken, String token);

    /**
     * 验证接口权限令牌
     * @param asToken
     */
    void validateKycAccessToken(AccessToken asToken);
}
