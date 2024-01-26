package com.uaepay.gateway.cgs.app.template.testtool.test.provider;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import lombok.Data;

import java.util.Date;

@Service
public class HelloProvider extends AbstractClientServiceProvider<HelloProvider.Request, HelloProvider.Response> {

    @Override
    public String getServiceCode() {
        return "/hello";
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
        String name;
        Date requestTime;
    }

    @Data
    public static class Response {
        String greet;
        Date requestTime;
    }

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) {
        ParameterValidate.assertNotNull("body", request.getBody());
        ParameterValidate.assertNotBlank("name", request.getBody().getName());
        ParameterValidate.assertNotNull("requestTime", request.getBody().getRequestTime());
    }

    @Override
    public ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) {
        Response result = new Response();
        result.setGreet("hello " + request.getBody().getName());
        result.setRequestTime(request.getBody().getRequestTime());
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, GatewayReturnCode.SUCCESS.getMessage(), result);
    }

}
