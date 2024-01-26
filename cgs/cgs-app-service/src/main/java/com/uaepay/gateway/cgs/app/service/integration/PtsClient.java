package com.uaepay.gateway.cgs.app.service.integration;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.pts.ext.facade.AccessTokenFacade;
import com.uaepay.pts.ext.facade.GuardTokenFacade;
import com.uaepay.pts.ext.facade.request.AccessTokenQueryRequest;
import com.uaepay.pts.ext.facade.request.GetTokenValueRequest;
import com.uaepay.pts.ext.facade.request.RefreshTokenRequest;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import com.uaepay.pts.ext.facade.response.GuardTokenValueResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import static com.uaepay.gateway.cgs.app.service.common.CgsReturnCode.MEMBER_NOT_SAME_ERR;

/**
 * pts client  .
 * <p>
 *
 * @author yusai
 * @date 2020-08-17 22:48.
 */

@Slf4j
@Component
public class PtsClient {

    private static final String TOKEN_NOT_EXISTS = "7004";

    private static final String MEMBER_NOT_SAME = "69408";

    @Reference
    GuardTokenFacade guardTokenFacade;

    @Reference
    AccessTokenFacade accessTokenFacade;

    public AccessTokenResponse getAccessToken(String accessToken) {

        AccessTokenQueryRequest query = new AccessTokenQueryRequest();
        query.setAccessToken(accessToken);

        AccessTokenResponse response;
        try {
            response = accessTokenFacade.getAccessTokenV2(query);
        } catch (Throwable e) {
            log.error("cgs -> pts accessTokenFacade.getAccessToken",e);
            throw new GatewayFailException(GatewayReturnCode.SYSTEM_ERROR);
        }

        // token 等于空 或者 报不存在的code
        if (response == null || StringUtils.equals(TOKEN_NOT_EXISTS, response.getResponseCode())) {
            throw new GatewayFailException(GatewayReturnCode.INVALID_TOKEN);
        }
        if (!response.isSuccess()) {
            log.info("[getAccessToken not success, code: {}, message: {} ]",
                    response.getResponseCode(), response.getResponseMessage());
            if (MEMBER_NOT_SAME.equals(response.getResponseCode())) {
                throw new GatewayFailException(MEMBER_NOT_SAME_ERR);
            }
            if (StringUtils.isNotBlank(response.getUnityResultCode())) {
                throw new GatewayFailException(GatewayReturnCode.SYSTEM_ERROR, response.getUnityResultCode());
            }
            throw new GatewayFailException(GatewayReturnCode.SYSTEM_ERROR);
        }

        if (response.getSqueezeOut() != null && response.getSqueezeOut() == YesNoEnum.YES) {
            throw new GatewayFailException(GatewayReturnCode.SQUEEZE_OUT);
        }
        return response;
    }

    /**
     * 获取guard token
     *
     * @param deviceId
     * @param identity
     * @return
     */
    public GuardTokenValueResponse getGuardTokenValue(String deviceId,String identity,String partnerId) {


        GetTokenValueRequest request = new GetTokenValueRequest()
                .setDeviceId(deviceId)
                .setIdentity(identity)
                .setPartnerId(partnerId);
        try {

            log.info("cgs -> pts getGuardTokenValue request:{}",request);

            GuardTokenValueResponse response = guardTokenFacade.getGuardTokenValue(request);

            log.info("cgs -> pts getGuardTokenValue response:{}",response);

            if(response.isSuccess()) {
                return response;
            }

        }catch (Throwable e) {
            log.error("调用pts 获取guardToken value 异常:",e);
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR);
        }

        log.error("调用pts 获取guardToken value 返回失败");
        throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR);
    }

    public AccessTokenResponse refreshAccessToken(String token) {

        try {
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setOrigAccessToken(token);

            log.info("cgs -> pts refresh AccessToken request:{}",request);
            AccessTokenResponse response = accessTokenFacade.refreshAccessToken(request);
            log.info("cgs -> pts refresh AccessToken response:{}",response);

            if(response.isSuccess()) {
                return response;
            }
            // 记录不存在
            if("7004".equalsIgnoreCase(response.getResponseCode())) {
                throw new ErrorException(GatewayReturnCode.INVALID_TOKEN);
            }

        }catch (Throwable e) {
            log.error("调用pts 获取refreshAccessToken 异常:",e);
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR);
        }
        throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR);
    }
}
