package com.uaepay.gateway.cgs.app.template.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;

/**
 * 客户端服务请求
 * 
 * @author zc
 * @param <T>
 *            业务参数对象
 */
public class ClientServiceRequest<T> {

    private String langType;

    private PlatformType platform;

    private Header header;

    private AccessMember accessMember;

    private String accessToken;

    private T body;

    /** 语言场景，可空 */
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

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
