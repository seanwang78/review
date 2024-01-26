package com.uaepay.gateway.cgs.test.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.util.ShaUtil;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.common.facade.enums.LangType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class RequestBuilder {

    public RequestBuilder() {
        lang(LangType.ENGLISH.getCode());
        platform(PlatformType.Android.getCode());
    }

    public RequestBuilder(String service) {
        this();
        this.service = service;
    }

    String service = "";

    HttpHeaders headers = new HttpHeaders();

    JSONObject body = new JSONObject();

    String bodyRaw;

    String accessKey;

    public RequestBuilder lang(String lang) {
        headers.set(GatewayConstants.LANG, lang);
        return this;
    }

    public RequestBuilder platform(String platform) {
        headers.set(GatewayConstants.PLATFORM, platform);
        return this;
    }

    public RequestBuilder deviceId(String deviceId) {
        headers.set(GatewayConstants.DEVICE_FINGERPRINT, deviceId);
        return this;
    }

    public RequestBuilder body(String body) {
        bodyRaw = body;
        return this;
    }

    public RequestBuilder accessToken(String accessToken) {
        headers.set(GatewayConstants.ACCESS_TOKEN, accessToken);
        return this;
    }

    public RequestBuilder referer(String referer) {
        headers.set(GatewayConstants.REFERER, referer);
        return this;
    }

    public RequestBuilder accessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public RequestBuilder ucid(String ucid) {
        headers.set(HttpHeaderKey.X_UCID,ucid);
        return this;
    }

    public RequestBuilder accessInfo(MockAccess accessInfo) {
        accessToken(accessInfo.getAccessToken());
        accessKey(accessInfo.getAccessKey());
        return this;
    }

    public RequestBuilder sign(String sign) {
        headers.set(GatewayConstants.SIGN, sign);
        return this;
    }

    public RequestBuilder param(String key, Object value) {
        body.put(key, value);
        return this;
    }

    public RequestBuilder template() {
        return this;
    }

    public String getService() {
        return service;
    }

    public HttpEntity<String> build() {
        String bodyString = null;
        if (bodyRaw != null) {
            bodyString = bodyRaw;
        }
        if (bodyString == null) {
            bodyString = body.isEmpty() ? "" : JSON.toJSONString(body);
        }
        if (accessKey != null) {
            System.out.println("plain: " + (bodyString + accessKey));
            sign(ShaUtil.getSha256(bodyString + accessKey, GatewayConstants.DEFAULT_CHARSET));
        }
        return new HttpEntity<>(bodyString, headers);
    }

}
