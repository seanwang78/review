package com.uaepay.gateway.cgs.app.facade.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;
import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 网关响应
 *
 * @param <T>
 *            业务响应体
 */
public class ClientServiceResponse<T> {

    private ClientServiceResponseHead head;

    private T body;

    @JsonIgnore
    private Map<String, String> sessionMap;


    public static <K> ClientServiceResponse<K> buildByData(String code, String message,Map<String,Object> data) {
        ClientServiceResponse<K> response = build(code, message);
        response.getHead().setData(data);
        return response;
    }

    public static <K> ClientServiceResponse<K> build(String code, String message, Map<String, Object> extParam,
        K body) {
        ClientServiceResponse<K> result = new ClientServiceResponse<>();
        result.setHead(new ClientServiceResponseHead(code, message, extParam));
        result.setBody(body);
        return result;
    }

    public static <K> ClientServiceResponse<K> build(String code, String message, K body) {
        return build(code, message, null, body);
    }

    public static <K> ClientServiceResponse<K> build(String code, String message) {
        return build(code, message, null, null);
    }

    public static <K> ClientServiceResponse<K> build(CodeEnum returnCode, String message, K body) {
        return build(returnCode.getCode(), message, null, body);
    }

    public static <K> ClientServiceResponse<K> build(CodeEnum returnCode, String message, Map<String, Object> extParam,
        K body) {
        return build(returnCode.getCode(), message, extParam, body);
    }

    public static <K> ClientServiceResponse<K> build(CodeEnum returnCode, String message) {
        return build(returnCode.getCode(), message, null, null);
    }

    public static <K> ClientServiceResponse<K> build(CodeMessageEnum returnCode) {
        return build(returnCode.getCode(), returnCode.getMessage(), null, null);
    }

    public ClientServiceResponse<T> codeMessage(CodeEnum returnCode, String message) {
        if (head == null) {
            head = new ClientServiceResponseHead();
        }
        head.setCode(returnCode.getCode());
        head.setMsg(message);
        return this;
    }

    public ClientServiceResponse<T> body(T body) {
        this.body = body;
        return this;
    }

    public ClientServiceResponse<T> session(String key, String value) {
        if (sessionMap == null) {
            sessionMap = new HashMap<>();
        }
        sessionMap.put(key, value);
        return this;
    }

    public ClientServiceResponseHead getHead() {
        return head;
    }

    public void setHead(ClientServiceResponseHead head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Map<String, String> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, String> sessionMap) {
        this.sessionMap = sessionMap;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE).setExcludeFieldNames("sessionMap")
            .toString();
    }

}
