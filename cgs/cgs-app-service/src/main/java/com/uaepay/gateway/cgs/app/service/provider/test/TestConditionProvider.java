package com.uaepay.gateway.cgs.app.service.provider.test;

import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/9/21
 * @since 0.1
 */
@Service
public class TestConditionProvider
        extends AbstractClientServiceProvider<TestConditionProvider.Request, TestConditionProvider.Response> {
    @Override
    protected ApiType getApiType() {
        return ApiType.CONDITION;
    }

    @Override
    protected Class<TestConditionProvider.Request> getReqBodyClazz() {
        return Request.class;
    }

    @Override
    protected void processValidate(ClientServiceRequest<TestConditionProvider.Request> request) throws GatewayFailException {

    }

    @Override
    protected ClientServiceResponse<TestConditionProvider.Response> processService(ClientServiceRequest<TestConditionProvider.Request> request) throws GatewayFailException {
        TestConditionProvider.Response result = new TestConditionProvider.Response();
        result.setGreet("hello " + request.getBody().getName());
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, "申请成功", result);
    }

    @Override
    public String getServiceCode() {
        return "/test/condition";
    }

    @Data
    public static class Request{
        private String name;
    }

    @Data
    public static class Response{
        private String greet;
    }
}
