package com.uaepay.gateway.cgs.app.service.provider.basic;

import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/1/15
 * @since 0.1
 */
@Component
public class CheckLogonProvider extends AbstractClientServiceProvider<Void, CheckLogonProvider.Response> {
    public static final String SERVICE_CODE = "/common/checkLogon";

    @Override
    protected ApiType getApiType() {
        return ApiType.AUTHED;
    }

    @Override
    protected Class<Void> getReqBodyClazz() {
        return Void.class;
    }

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest request) throws GatewayFailException {
        Response response = new Response();
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, null, response);
    }

    @Override
    protected void processValidate(ClientServiceRequest request) throws GatewayFailException {
    }

    @Override
    public String getServiceCode() {
        return SERVICE_CODE;
    }

    @Data
    class Response implements Serializable {
        /** Y:已经登陆 N:未登陆  */
        private String isLogon;
    }
}
