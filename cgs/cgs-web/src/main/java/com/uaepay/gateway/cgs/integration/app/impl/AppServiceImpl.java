package com.uaepay.gateway.cgs.integration.app.impl;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.gateway.cgs.app.facade.api.ClientGatewayFacade;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayResponse;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domain.api.ApiVersion;
import com.uaepay.gateway.cgs.integration.app.AppService;
import com.uaepay.gateway.cgs.integration.app.AppServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zc
 */
@Service
public class AppServiceImpl implements AppService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppServiceFactory gatewayServiceFactory;

    @Override
    public ClientGatewayResponse doService(ReceiveOrderContext context) {
        ClientGatewayFacade service = gatewayServiceFactory.getOrCreate(context.getApiConfig().getAppCode());
        ClientGatewayRequest request = new ClientGatewayRequest();
        request.setLangType(context.getLang().getCode());
        request.setPlatform(context.getPlatformType());
        request.setService(context.getService());
        request.setApiType(context.getApiConfig().getApiType());
        request.setHeader(context.getHeader());
        request.setLangScene(context.getLangScene());

        if (request.getApiType() == ApiType.AUTHED || request.getApiType() == ApiType.CONDITION) {
            request.setAccessMember(context.getAccessMember());
            request.setAccessToken(context.getAccessToken());
        }
        if (context.getJsonBody() != null) {
            request.setBody(context.getJsonBody().toJSONString());
        }
        if (context.isDebug()) {
            logger.info("app请求: {}", request);
        }
        ClientGatewayResponse response = processService(context.getApiConfig().getApiVersion(), service, request);
        if (context.isDebug()) {
            logger.info("app响应: {}", response);
        }
        return response;
    }

    private ClientGatewayResponse processService(ApiVersion apiVersion, ClientGatewayFacade service,
        ClientGatewayRequest request) {
        if (apiVersion == null || apiVersion == ApiVersion.V1) {
            String response = service.doService(request);
            return new ClientGatewayResponse(response, null);
        } else if (apiVersion == ApiVersion.V2) {
            return service.doServiceV2(request);
        } else {
            throw new ErrorException("api版本异常");
        }
    }

}
