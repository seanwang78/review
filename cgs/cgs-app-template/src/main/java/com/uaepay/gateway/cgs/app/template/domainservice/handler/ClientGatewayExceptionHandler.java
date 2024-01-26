package com.uaepay.gateway.cgs.app.template.domainservice.handler;

import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;

/**
 * 客户端网关异常处理器
 * 
 * @author zc
 */
public interface ClientGatewayExceptionHandler {

    /**
     * 处理异常
     * 
     * @param gatewayRequest
     *            网关请求
     * @param e
     *            异常
     * @return 如果非空，则返回给网关；如果不处理异常，返回null
     */
    ClientServiceResponse<?> handle(ClientGatewayRequest gatewayRequest, Throwable e);

}
