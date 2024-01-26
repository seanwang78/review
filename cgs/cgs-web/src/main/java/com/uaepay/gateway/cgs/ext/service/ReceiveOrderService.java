package com.uaepay.gateway.cgs.ext.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网关请求处理服务
 * @author zc
 */
public interface ReceiveOrderService {

    /**
     * 处理http请求
     */
    void processHttpRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

}
