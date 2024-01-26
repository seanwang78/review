package com.uaepay.gateway.cgs.test.common;

import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class ResponseChecker {

    public static final String CODE = "code";
    public static final String MESSAGE = "msg";
    public static final String BODY = "body";

    public ResponseChecker(String response) {
        Assert.assertNotNull("响应不能为空", response);
        this.responseJson = JSON.parseObject(response);
        this.headJson = responseJson.getJSONObject("head");
        this.bodyJson = responseJson.getJSONObject("body");
        // Assert.assertNotNull("响应内容不能为空", bodyJson);
    }

    JSONObject responseJson;
    JSONObject headJson;
    JSONObject bodyJson;

    public ResponseChecker success() {
        codeMessage(GatewayReturnCode.SUCCESS, "申请成功");
        return this;
    }

    public ResponseChecker codeMessage(GatewayReturnCode code, String message) {
        Assert.assertNotNull("head非空", headJson);
        Assert.assertEquals("返回码", code.getCode(), headJson.getString(CODE));
        Assert.assertEquals("返回信息", message, headJson.getString(MESSAGE));
        return this;
    }

    public ResponseChecker codeNoMessage(GatewayReturnCode code) {
        Assert.assertNotNull("head非空", headJson);
        Assert.assertEquals("返回码", code.getCode(), headJson.getString(CODE));
        Assert.assertFalse("返回信息", headJson.containsKey(MESSAGE));
        return this;
    }

    public ResponseChecker noBody() {
        Assert.assertFalse("body为空", responseJson.containsKey(BODY));
        return this;
    }

    public ResponseChecker emptyBody() {
        Assert.assertTrue("body存在", responseJson.containsKey(BODY));
        Assert.assertTrue("body为empty", bodyJson.isEmpty());
        return this;
    }

    public ResponseChecker param(String name, String value) {
        Assert.assertEquals("业务参数: " + name, value, bodyJson.getString(name));
        return this;
    }

    public ResponseChecker paramNotBlank(String name) {
        Assert.assertTrue("业务参数非空: " + name, StringUtils.isNotBlank(bodyJson.getString(name)));
        return this;
    }

    public ResponseChecker paramStartsWith(String name, String prefix) {
        paramNotBlank(name);
        Assert.assertTrue("业务参数前缀错误: " + name, bodyJson.getString(name).startsWith(prefix));
        return this;
    }

    public String getStringParam(String name) {
        return bodyJson.getString(name);
    }

    // public ResponseChecker tokenRefreshed() {
    // Assert.assertTrue("基本参数非空: access_token",
    // StringUtils.isNotBlank(bodyJson.getString("access_token")));
    // Assert.assertTrue("基本参数非空: access_key",
    // StringUtils.isNotBlank(bodyJson.getString("access_key")));
    // return this;
    // }

}
