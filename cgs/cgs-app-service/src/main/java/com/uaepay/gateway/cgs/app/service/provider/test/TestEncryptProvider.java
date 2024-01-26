package com.uaepay.gateway.cgs.app.service.provider.test;

import org.springframework.stereotype.Service;

import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import lombok.Data;

@Service
public class TestEncryptProvider
    extends AbstractClientServiceProvider<TestEncryptProvider.Request, TestEncryptProvider.Response> {

    @Override
    public String getServiceCode() {
        return "/test/encrypt";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.AUTHED;
    }

    @Override
    protected Class<Request> getReqBodyClazz() {
        return Request.class;
    }

    @Data
    public static class Request {
        String password;
    }

    @Data
    public static class Response {
        String uesPassword;
    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) {}

    @Override
    public ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) {
        Response result = new Response();
        if (request.getBody() != null) {
            result.setUesPassword(request.getBody().getPassword());
        }
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, GatewayReturnCode.SUCCESS.getMessage(), result);
    }

}
