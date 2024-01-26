package com.uaepay.gateway.cgs.app.facade.domain;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;

/**
 * 网关请求
 * 
 * @author zc
 */
public class ClientGatewayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String langType;

    private PlatformType platform;

    private String service;

    private String body;

    private ApiType apiType;

    private Header header;

    private AccessMember accessMember;

    private String accessToken;

    private String langScene;

    public String getLangType() {
        return langType;
    }

    public void setLangType(String langType) {
        this.langType = langType;
    }

    public PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformType platform) {
        this.platform = platform;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ApiType getApiType() {
        return apiType;
    }

    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }

    public AccessMember getAccessMember() {
        return accessMember;
    }

    public void setAccessMember(AccessMember accessMember) {
        this.accessMember = accessMember;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getLangScene() {
        return langScene;
    }

    public void setLangScene(String langScene) {
        this.langScene = langScene;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

}
