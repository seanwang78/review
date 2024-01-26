package com.uaepay.gateway.cgs.app.facade.api;

import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayResponse;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;

/**
 * 客户端网关服务门面
 * 
 * @author zc
 */
public interface ClientGatewayFacade {

    /**
     * 处理服务，如果不指定API配置的扩展参数: version，则调用此方法
     * 
     * @see ClientServiceResponse
     * @return JSONString of ClientServiceResponse<T>
     */
    String doService(ClientGatewayRequest request);

    /**
     * 处理服务V2，需要指定API配置的扩展参数: version=2.0
     * 
     * @param request
     *            网关请求
     * @return 网关响应
     */
    ClientGatewayResponse doServiceV2(ClientGatewayRequest request);

}
