package com.uaepay.gateway.cgs.app.service.provider.basic;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetSaltWithoutTokenProvider extends AbstractClientServiceProvider<GetSaltWithoutTokenProvider.Request, GetSaltWithoutTokenProvider.Response> {

    @Override
    public String getServiceCode() {
        return "/common/getAppSalt";
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        String salt;
    }

    @Autowired
    AccessTokenService accessTokenService;

    @Override
    protected void processValidate(ClientServiceRequest<Request> request) {}

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest<Request> request) {
        ParameterValidate.assertNotBlank("saltKey", request.getBody().getSaltKey());
        String salt = accessTokenService.generateSalt(request.getBody().getSaltKey());
        if (StringUtils.isBlank(salt)) {
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR, "获取盐为空");
        }
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, null, new Response(salt));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String saltKey;
    }

}
