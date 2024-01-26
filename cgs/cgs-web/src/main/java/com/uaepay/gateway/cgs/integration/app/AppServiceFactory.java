package com.uaepay.gateway.cgs.integration.app;

import com.uaepay.gateway.cgs.app.facade.api.ClientGatewayFacade;

/**
 * 网关服务工厂
 * @author zc
 */
public interface AppServiceFactory {

    ClientGatewayFacade getOrCreate(String appCode);

}
