package com.uaepay.gateway.cgs.app.service.provider.h5app;

import java.nio.charset.StandardCharsets;

import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.common.util.ShaUtil;
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
public class H5AppAuthVerifyProvider extends AbstractClientServiceProvider<H5AppAuthVerifyProvider.Request, Void> {

    private static final String ACCESS_TOKEN = "Access-Token";

    @Override
    public String getServiceCode() {
        return "/h5app/auth/verify";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.UNAUTHED;
    }

    @Override
    protected Class<Request> getReqBodyClazz() {
        return Request.class;
    }

    public H5AppAuthVerifyProvider(VerifyTokenService verifyTokenService, H5AppProperties h5AppProperties) {
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

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) throws GatewayFailException {
        Request body = request.getBody();
        ParameterValidate.assertNotNull("body", body);
        ParameterValidate.assertNotBlank("vt", body.getVerifyToken());
        ParameterValidate.assertNotBlank("cv", body.getCodeVerifier());
    }

    @Override
    protected ClientServiceResponse<Void> processService(ClientServiceRequest<Request> request)
        throws GatewayFailException {
        Request body = request.getBody();

        logger.info("h5.checkSwitch = {}",h5AppProperties.isCheckSwitch());

        if(request.getPlatform() == PlatformType.MINIAPP) {
            // 小程序无域名，所以不校验
        }else{
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
        return new ClientServiceResponse<Void>().codeMessage(GatewayReturnCode.SUCCESS, null).session(ACCESS_TOKEN,
            accessToken);
    }

}
