package com.uaepay.gateway.cgs.app.service.provider.h5app;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.service.common.UriUtil;
import com.uaepay.gateway.cgs.app.service.constants.CgsServiceLangCode;
import com.uaepay.gateway.cgs.app.service.domain.properties.H5AppProperties;
import com.uaepay.gateway.cgs.app.service.provider.login.enums.VerifyScene;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.app.template.domain.VerifyToken;
import com.uaepay.gateway.common.app.template.domainservice.token.VerifyTokenService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zc
 */
@Service
public class H5AppAuthApplyProvider
    extends AbstractClientServiceProvider<H5AppAuthApplyProvider.Request, H5AppAuthApplyProvider.Response> {

    @Override
    public String getServiceCode() {
        return "/h5app/auth/apply";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.AUTHED;
    }

    public H5AppAuthApplyProvider(VerifyTokenService verifyTokenService, H5AppProperties h5AppProperties) {
        this.verifyTokenService = verifyTokenService;
        this.h5AppProperties = h5AppProperties;
    }

    private VerifyTokenService verifyTokenService;

    private H5AppProperties h5AppProperties;

    @Override
    protected Class<Request> getReqBodyClazz() {
        return Request.class;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        /** 客户端自行生成一个可见的ASCII字符串，并使用SHA256进行Hash，编码成16进制字符串 */
        String codeChallenge;

        /** h5app的域名 */
        String appDomain;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        /** 验证令牌，用于后续验证时关联验证信息 */
        String verifyToken;

    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) throws GatewayFailException {
        Request body = request.getBody();
        ParameterValidate.assertNotNull("body", body);
        ParameterValidate.assertNotBlank("codeChallenge", body.getCodeChallenge());
        ParameterValidate.assertNotBlank("appDomain", body.getAppDomain());
    }

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request)
        throws GatewayFailException {
        Request body = request.getBody();

        // 域名白名单校验
        if (h5AppProperties.getAllowDomains() == null
            || (!h5AppProperties.getAllowDomains().containsValue(UriUtil.getDomain(body.getAppDomain())) && h5AppProperties.isCheckSwitch())) {
            throw new GatewayFailException(GatewayReturnCode.BIZ_CHECK_FAIL,
                CgsServiceLangCode.H5APP_DOMAIN_NOT_ALLOWED);
        }

        // 保存验证令牌
        VerifyToken.VerifyInfo verifyInfo =
            new VerifyToken.VerifyInfo(null, VerifyScene.H5APP_AUTH.name(), null, body.getCodeChallenge());
        VerifyToken<String> verifyToken =
            new VerifyToken<>(verifyInfo, h5AppProperties.getVerifyAllowCount(), request.getAccessToken());
        String token = verifyTokenService.generateToken(verifyToken, h5AppProperties.getTokenTtl());

        Response response = new Response();
        response.setVerifyToken(token);
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, null, response);
    }

}
