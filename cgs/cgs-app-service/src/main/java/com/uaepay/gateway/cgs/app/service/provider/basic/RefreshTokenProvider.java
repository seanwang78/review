package com.uaepay.gateway.cgs.app.service.provider.basic;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.service.domain.response.NewVersionResponse;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.app.service.integration.PtsClient;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 刷新token
 */
@Service
public class RefreshTokenProvider extends AbstractClientServiceProvider<RefreshTokenProvider.Request, RefreshTokenProvider.Response> {

    public static final String SERVICE_CODE = "/common/refreshToken";

    @Autowired
    PtsClient ptsClient;

    @Autowired
    AccessTokenService accessTokenService;

    @Override
    protected ApiType getApiType() {
        return ApiType.AUTHED;
    }

    @Override
    protected Class<Request> getReqBodyClazz() {
        return Request.class;
    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) throws GatewayFailException {
        ParameterValidate.assertNotNull("token",request.getBody().getToken());
    }

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) throws GatewayFailException {

        String bToken = request.getBody().getToken();
        String token = accessTokenService.analysisToken(bToken);

        AccessTokenResponse res = ptsClient.refreshAccessToken(token);

        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, null, new Response(res.getToken(),
                res.getAccessKey()));
    }

    @Override
    public String getServiceCode() {
        return SERVICE_CODE;
    }

    @Data
    public static class Request implements Serializable {

        private static final long serialVersionUID = -7517093355299557705L;

        private String token;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response implements Serializable {

        private static final long serialVersionUID = -6927321238865419472L;

        private String newToken;

        private String accessKey;
    }
}
