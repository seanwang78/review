package com.uaepay.gateway.cgs.app.service.provider.login;

import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.pts.ext.facade.AuthTokenFacade;
import com.uaepay.pts.ext.facade.request.AuthTokenRequest;
import com.uaepay.pts.ext.facade.response.AuthTokenKeyResponse;
import lombok.Data;
import lombok.ToString;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 拿授权token .
 * <p>
 *
 * @author yusai
 * @date 2019-11-14 16:48.
 */
@Component
public class MockAuthTokenProvider extends AbstractClientServiceProvider<MockAuthTokenProvider.Request, MockAuthTokenProvider.Response> {

    @Reference
    private AuthTokenFacade authTokenFacade;

    @Override
    public String getServiceCode() {
        return "/mockAuthToken";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.UNAUTHED;
    }

    @Override
    protected Class<Request> getReqBodyClazz() {
        return MockAuthTokenProvider.Request.class;
    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) throws GatewayFailException {

    }

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) throws GatewayFailException {
        AuthTokenRequest domain = new AuthTokenRequest();
        domain.setPartnerId(request.getBody().getPartnerId());
        domain.setDeviceId(request.getBody().getDeviceId());
        domain.setMobile(request.getBody().getMobile());
        domain.setIdentity(request.getBody().getIdentity());
        AuthTokenKeyResponse tokenKeyResponse =  authTokenFacade.applyAuthToken(domain);
        Response res = new Response();
        res.setAuthToken(tokenKeyResponse.getAuthToken());
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS,null,res);
    }

    @Data
    public static class Request implements Serializable{
        private String identity;
        @ToString.Exclude
        private String mobile;
        private String deviceId;
        private String partnerId;
    }

    @Data
    public static class Response implements Serializable {
        private String authToken;
    }

}
