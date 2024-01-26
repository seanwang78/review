package com.uaepay.gateway.cgs.app.service.provider.h5app;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ShaUtil;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.app.service.common.UriUtil;
import com.uaepay.gateway.cgs.app.service.constants.CgsServiceLangCode;
import com.uaepay.gateway.cgs.app.service.domain.properties.H5AppProperties;
import com.uaepay.gateway.cgs.app.service.integration.PtsClient;
import com.uaepay.gateway.cgs.app.service.provider.login.enums.VerifyScene;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.domain.VerifyToken;
import com.uaepay.gateway.common.app.template.domainservice.token.VerifyTokenService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 *
 */
@Service
public class H5AppAuthVerifyV2Provider extends AbstractClientServiceProvider<H5AppAuthVerifyV2Provider.Request, H5AppAuthVerifyV2Provider.Response> {

    private static final String ACCESS_TOKEN = "Access-Token";

    @Autowired
    private PtsClient ptsClient;

    @Override
    public String getServiceCode() {
        return "/h5app/auth/v2/verify";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.UNAUTHED;
    }

    @Override
    protected Class<Request> getReqBodyClazz() {
        return Request.class;
    }

    public H5AppAuthVerifyV2Provider(VerifyTokenService verifyTokenService, H5AppProperties h5AppProperties) {
        this.verifyTokenService = verifyTokenService;
        this.h5AppProperties = h5AppProperties;
    }

    private VerifyTokenService verifyTokenService;

    private H5AppProperties h5AppProperties;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        /** 验证令牌 */
        String verifyToken;

        /** 请求verifyToken阶段生成codeChallenge hash值的原文 */
        String codeVerifier;

    }

    @Data
    public static class Response {
        private String userId;
        private String token;
    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) throws GatewayFailException {
        Request body = request.getBody();
        ParameterValidate.assertNotNull("body", body);
        ParameterValidate.assertNotBlank("vt", body.getVerifyToken());
        ParameterValidate.assertNotBlank("cv", body.getCodeVerifier());
    }

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request)
        throws GatewayFailException {
        Request body = request.getBody();

        if(request.getPlatform() == PlatformType.MINIAPP ||
                request.getPlatform() == PlatformType.MINIAPP_BOITM_PAY ) {
            // 小程序无域名，所以不校验

        }else {
            // 域名白名单校验
            if (h5AppProperties.getAllowDomains() == null
                    || (!h5AppProperties.getAllowDomains().containsValue(UriUtil.getDomain(request.getHeader().getReferer()))
                    && h5AppProperties.isCheckSwitch())) {
                throw new GatewayFailException(GatewayReturnCode.BIZ_CHECK_FAIL,
                        CgsServiceLangCode.H5APP_DOMAIN_NOT_ALLOWED);
            }

        }

        String verifyCode = ShaUtil.getSha256(body.codeVerifier, StandardCharsets.UTF_8);
        VerifyToken.VerifyInfo verifyInfo =
            new VerifyToken.VerifyInfo(null, VerifyScene.H5APP_AUTH.name(), null, verifyCode);
        VerifyTokenService.VerifyResult<String> verifyResult =
            verifyTokenService.verifyToken(body.getVerifyToken(), verifyInfo, String.class);
        if (verifyResult.getStatus() != VerifyTokenService.VerifyStatus.SUCCESS) {
            throw new BizCheckFailException(verifyResult.getMessage());
        }

        String accessToken = verifyResult.getEntity();

        AccessTokenResponse at = ptsClient.getAccessToken(accessToken);
        Response resBody = new Response();
        resBody.setUserId(ShaUtil.getSha256(at.getMemberId()));
        // botim-pay 小程序返回token
        if(request.getPlatform() == PlatformType.MINIAPP_BOITM_PAY) {
            resBody.setToken(accessToken);
        }
        return new ClientServiceResponse<Response>().codeMessage(GatewayReturnCode.SUCCESS, null).session(ACCESS_TOKEN,
            accessToken).body(resBody);
    }
}
