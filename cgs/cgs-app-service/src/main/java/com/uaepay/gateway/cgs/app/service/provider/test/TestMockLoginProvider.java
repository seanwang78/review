package com.uaepay.gateway.cgs.app.service.provider.test;

import com.uaepay.basis.beacon.common.exception.fail.BizCheckFailException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import com.uaepay.pts.ext.facade.AccessTokenFacade;
import com.uaepay.pts.ext.facade.model.KeyInfo;
import com.uaepay.pts.ext.facade.request.ApplyTokenRequest;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestMockLoginProvider
    extends AbstractClientServiceProvider<TestMockLoginProvider.Request, TestMockLoginProvider.Response> {

    @Override
    public String getServiceCode() {
        return "/test/mockLogin";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.UNAUTHED;
    }

    @Override
    protected Class<Request> getReqBodyClazz() {
        return Request.class;
    }

    @Data
    public static class Request {
        @ToString.Exclude
        String mobile;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        String accessToken;
        String accessKey;
    }

    public TestMockLoginProvider(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    private AccessTokenService accessTokenService;

    @Value("${mockMemberId}")
    private String mockMemberId;

    @Value("${mockLogin.enabled:false}")
    private boolean mockLoginEnabled;

    @Reference
    private AccessTokenFacade accessTokenFacade;
    @Override
    protected void processValidate(ClientServiceRequest<Request> request) {
        ParameterValidate.assertNotNull("body", request.getBody());
        ParameterValidate.assertNotBlank("mobile", request.getBody().getMobile());
        if (!mockLoginEnabled) {
            throw new BizCheckFailException("mock登陆开关未开启");
        }
    }

    @Override
    public ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) {

        ApplyTokenRequest applyTokenRequest = new ApplyTokenRequest();

        KeyInfo keyInfo = new KeyInfo();

        keyInfo.setIdentity(mockMemberId);
        keyInfo.setPlatform(request.getPlatform().getCode());

        applyTokenRequest.setKeyInfo(keyInfo);

        applyTokenRequest.setDeviceId(request.getHeader().getDeviceId());
        applyTokenRequest.setMemberId(mockMemberId);
        // todo pts 去掉此参数
        applyTokenRequest.setAuthToken("aaaa");
        applyTokenRequest.setPartnerId("200000000777");

        AccessTokenResponse accessResponse = accessTokenFacade.applyAccessToken(applyTokenRequest);

        Response response = new Response(accessResponse.getAccessToken(), accessResponse.getAccessKey());
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, GatewayReturnCode.SUCCESS.getMessage(), response);
    }

}
