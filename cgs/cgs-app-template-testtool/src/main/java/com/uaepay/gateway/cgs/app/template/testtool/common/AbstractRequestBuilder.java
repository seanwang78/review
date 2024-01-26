package com.uaepay.gateway.cgs.app.template.testtool.common;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.common.facade.enums.LangType;

/**
 * CGS抽象请求建造器
 * 
 * @author zc
 * @param <BD>
 *            自定义建造器
 */
public class AbstractRequestBuilder<BD extends AbstractRequestBuilder> {

    protected ClientGatewayRequest result = new ClientGatewayRequest();

    protected ObjectBuilder body = new ObjectBuilder();

    protected String bodyRaw;

    public BD lang(LangType lang) {
        result.setLangType(lang.getCode());
        return (BD)this;
    }

    public BD platform(PlatformType platform) {
        result.setPlatform(platform);
        return (BD)this;
    }

    public BD service(String service) {
        result.setService(service);
        return (BD)this;
    }

    public BD apiType(ApiType apiType) {
        result.setApiType(apiType);
        return (BD)this;
    }

    /**
     * @see HeaderBuilder
     */
    public BD header(Header header) {
        result.setHeader(header);
        return (BD)this;
    }

    public BD deviceId(String deviceId) {
        if (result.getHeader() == null) {
            result.setHeader(new Header());
        }
        result.getHeader().setDeviceId(deviceId);
        return (BD)this;
    }

    public BD hostApp(String hostApp) {
        if (result.getHeader() == null) {
            result.setHeader(new Header());
        }
        result.getHeader().setHostApp(hostApp);
        return (BD)this;
    }

    public BD memberId(String memberId) {
        if (result.getAccessMember() == null) {
            result.setAccessMember(new AccessMember());
        }
        result.getAccessMember().setMemberId(memberId);
        return (BD)this;
    }

    /**
     * @see com.uaepay.gateway.cgs.app.template.testtool.common.AccessMemberBuilder
     */
    public BD accessMember(AccessMember accessMember) {
        result.setAccessMember(accessMember);
        return (BD)this;
    }

    public BD accessToken(String accessToken) {
        result.setAccessToken(accessToken);
        return (BD)this;
    }

    public BD langScene(String langScene) {
        result.setLangScene(langScene);
        return (BD)this;
    }

    public BD body(String body) {
        bodyRaw = body;
        return (BD)this;
    }

    public BD param(String key, Object value) {
        body.param(key, value);
        return (BD)this;
    }

    public BD paramDate(String key, Date date) {
        body.paramDate(key, date);
        return (BD)this;
    }

    /**
     * 设置时间参数
     *
     * @param key
     *            参数名称
     * @param date
     *            日期
     * @param timeZoneId
     *            时区ID，ex. +04:00,-07:00
     */
    public BD paramDate(String key, Date date, String timeZoneId) {
        body.paramDate(key, date, timeZoneId);
        return (BD)this;
    }

    public BD paramDateNow(String key) {
        body.paramDateNow(key);
        return (BD)this;
    }

    public ClientGatewayRequest build() {
        if (bodyRaw != null) {
            result.setBody(bodyRaw);
        } else {
            result.setBody(JSON.toJSONString(body.build()));
        }
        return result;
    }

}
