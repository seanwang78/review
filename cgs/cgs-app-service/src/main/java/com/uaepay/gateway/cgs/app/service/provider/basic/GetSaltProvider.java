package com.uaepay.gateway.cgs.app.service.provider.basic;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.app.template.domain.ClientServiceRequest;
import com.uaepay.gateway.cgs.app.template.domainservice.service.AbstractClientServiceProvider;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
public class GetSaltProvider extends AbstractClientServiceProvider<Void, GetSaltProvider.Response> {

    @Override
    public String getServiceCode() {
        return "/common/getSalt";
    }

    @Override
    protected ApiType getApiType() {
        return ApiType.AUTHED;
    }

    @Override
    protected Class<Void> getReqBodyClazz() {
        return null;
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
    protected void processValidate(ClientServiceRequest<Void> request) {}

    @Override
    protected ClientServiceResponse<Response> processService(ClientServiceRequest<Void> request) {
        String salt = accessTokenService.generateSalt(request.getAccessToken());
        if (StringUtils.isBlank(salt)) {
            throw new ErrorException(GatewayReturnCode.SYSTEM_ERROR, "获取盐为空");
        }
        return ClientServiceResponse.build(GatewayReturnCode.SUCCESS, null, new Response(salt));
    }

}
