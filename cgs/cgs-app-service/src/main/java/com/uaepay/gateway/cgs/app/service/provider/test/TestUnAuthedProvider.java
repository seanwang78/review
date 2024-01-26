package com.uaepay.gateway.cgs.app.service.provider.test;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import lombok.Data;

@Service
public class TestUnAuthedProvider
    extends AbstractClientServiceProvider<TestUnAuthedProvider.Request, TestUnAuthedProvider.Response> {

    private int count;

    @Override
    public String getServiceCode() {
        return "/test/unauthed";
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
        String name;
    }

    @Data
    public static class Response {
        String greet;
        int count;
    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) {
        ParameterValidate.assertNotNull("body", request.getBody());
        ParameterValidate.assertNotBlank("name", request.getBody().getName());
    }

    @Override
    public ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) {
        Response result = new Response();
        result.setGreet("hello " + request.getBody().getName());
        result.setCount(count++);
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, GatewayReturnCode.SUCCESS.getMessage(), result);
    }

}
