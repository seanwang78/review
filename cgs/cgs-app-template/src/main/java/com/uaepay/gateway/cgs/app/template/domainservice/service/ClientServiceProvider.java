package com.uaepay.gateway.cgs.app.template.domainservice.service;

import com.uaepay.basis.beacon.common.template.CodeService;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;

/**
 * 客户端网关服务提供者
 * 
 * @author zc
 */
public interface ClientServiceProvider extends CodeService {

    /**
     * 处理服务
     * 
     * @param request
     *            客户端网关请求
     * @return 客户端网关响应
     */
    ClientServiceResponse<?> doService(ClientGatewayRequest request);

}
