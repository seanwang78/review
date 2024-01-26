package com.uaepay.gateway.cgs.app.facade.domain;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 客户端网关响应V2
 * 
 * @author zc
 */
public class ClientGatewayResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    public ClientGatewayResponse() {}

    public ClientGatewayResponse(String response, Map<String, String> sessionMap) {
        this.response = response;
        this.sessionMap = sessionMap;
    }

    /**
     * JSONString of ClientServiceResponse<T>
     * 
     * @see ClientServiceResponse
     */
    String response;

    /**
     * 如果不为空则在session设置值
     */
    Map<String, String> sessionMap;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Map<String, String> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, String> sessionMap) {
        this.sessionMap = sessionMap;
    }

    @Override
    public String toString() {
        ReflectionToStringBuilder builder =
            new ReflectionToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE).setExcludeFieldNames("sessionMap");
        if (sessionMap != null) {
            builder.append("sessionMapCount", sessionMap.size());
        }
        return builder.toString();
    }

}
