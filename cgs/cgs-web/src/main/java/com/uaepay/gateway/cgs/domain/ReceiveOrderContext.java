package com.uaepay.gateway.cgs.domain;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.util.SafePrint;
import com.uaepay.basis.beacon.common.util.SafeToStringBuilder;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.domain.api.ApiConfig;
import com.uaepay.gateway.common.facade.enums.LangType;

import lombok.Data;

@Data
public class ReceiveOrderContext {

    public ReceiveOrderContext(HttpServletRequest httpRequest, HttpServletResponse httpResponse, boolean debug,
        List<String> ignoreLoggingApis) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.debug = debug;
        this.ignoreLoggingApis = ObjectUtils.defaultIfNull(ignoreLoggingApis, Collections.emptyList());
    }

    /** 忽略日志的Api */
    @ToStringExclude
    private List<String> ignoreLoggingApis;

    @ToStringExclude
    HttpServletRequest httpRequest;

    @ToStringExclude
    HttpServletResponse httpResponse;

    boolean debug;

    /** 语言 */
    LangType lang;

    /** 服务 */
    String service;

    /** 平台类型，非空 */
    PlatformType platformType;

    /** 访问令牌，已授权接口专用 */
    @SafePrint
    String accessToken;

    /** 新token Base64格式 */
    String token;

    /** 签名，已授权接口专用 */
    String sign;

    /** 请求头 */
    Header header;

    /** 原始body信息，用于验签 */
    @ToStringExclude
    String body;

    /** 业务参数 */
    JSONObject jsonBody;

    /** API配置 */
    @ToStringExclude
    ApiConfig apiConfig;

    /** 用户信息，根据access_token获取 */
    @ToStringExclude
    AccessMember accessMember;

    public boolean isDebug() {
        if (ignoreLoggingApis.contains(service)) {
            return false;
        }

        if (apiConfig != null && apiConfig.getDebug() != null) {
            return apiConfig.getDebug();
        }
        return debug;
    }

    public String getLangScene() {
        return apiConfig != null ? apiConfig.getLangScene() : null;
    }

    public String toLogString() {
        return SafeToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

}
