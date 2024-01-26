package com.uaepay.gateway.cgs.app.template.testtool.common;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.ClientGatewayRequest;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.common.facade.enums.LangType;

/**
 * 请求构造器
 * 
 * @author zc
 */
public class RequestBuilder {

    public RequestBuilder() {}

    ClientGatewayRequest result = new ClientGatewayRequest();

    ObjectBuilder body = new ObjectBuilder();

    String bodyRaw;

    public RequestBuilder lang(LangType lang) {
        result.setLangType(lang.getCode());
        return this;
    }

    public RequestBuilder platform(PlatformType platform) {
        result.setPlatform(platform);
        return this;
    }

    public RequestBuilder service(String service) {
        result.setService(service);
        return this;
    }

    public RequestBuilder apiType(ApiType apiType) {
        result.setApiType(apiType);
        return this;
    }

    /**
     * @see HeaderBuilder
     */
    public RequestBuilder header(Header header) {
        result.setHeader(header);
        return this;
    }

    public RequestBuilder deviceId(String deviceId) {
        if (result.getHeader() == null) {
            result.setHeader(new Header());
        }
        result.getHeader().setDeviceId(deviceId);
        return this;
    }

    public RequestBuilder memberId(String memberId) {
        return accessMember(memberId);
    }

    public RequestBuilder accessMember(String memberId) {
        if (result.getAccessMember() == null) {
            result.setAccessMember(new AccessMember());
        }
        result.getAccessMember().setMemberId(memberId);
        return this;
    }

    /**
     * @see com.uaepay.gateway.cgs.app.template.testtool.common.AccessMemberBuilder
     */
    public RequestBuilder accessMember(AccessMember accessMember) {
        result.setAccessMember(accessMember);
        return this;
    }

    public RequestBuilder accessToken(String accessToken) {
        result.setAccessToken(accessToken);
        return this;
    }

    public RequestBuilder body(String body) {
        bodyRaw = body;
        return this;
    }

    public RequestBuilder param(String key, Object value) {
        body.param(key, value);
        return this;
    }

    public RequestBuilder paramDate(String key, Date date) {
        body.paramDate(key, date);
        return this;
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
    public RequestBuilder paramDate(String key, Date date, String timeZoneId) {
        body.paramDate(key, date, timeZoneId);
        return this;
    }

    public RequestBuilder paramDateNow(String key) {
        body.paramDateNow(key);
        return this;
    }

    public static RequestBuilder template(String service) {
        return new RequestBuilder().lang(LangType.ENGLISH).platform(PlatformType.Android).service(service)
            .apiType(ApiType.AUTHED);
    }

    public ClientGatewayRequest build() {
        if (bodyRaw != null) {
            result.setBody(bodyRaw);
        } else {
            result.setBody(JSON.toJSONString(body.build()));
        }
        return result;
    }

    /**
     * @see com.uaepay.gateway.cgs.app.template.testtool.common.AccessMemberBuilder
     */
    @Deprecated
    public class AccessMemberBuilder {

        AccessMember accessMember = new AccessMember();

        public AccessMember memberId(String memberId) {
            accessMember.setMemberId(memberId);
            return accessMember;
        }

        public AccessMember mobile(String mobile) {
            accessMember.setMobile(mobile);
            return accessMember;
        }

        public AccessMember identity(String identity) {
            accessMember.setIdentity(identity);
            return accessMember;
        }

        public AccessMember partnerId(String partnerId) {
            accessMember.setPartnerId(partnerId);
            return accessMember;
        }

        public AccessMember hasRealName(YesNoEnum hasRealName) {
            accessMember.setHasPayPwd(hasRealName);
            return accessMember;
        }

        public AccessMember hasPayPwd(YesNoEnum hasPayPwd) {
            accessMember.setHasPayPwd(hasPayPwd);
            return accessMember;
        }

        public AccessMember build() {
            return accessMember;
        }

    }
}
