package com.uaepay.gateway.cgs.app.template.testtool.common;

import org.junit.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;
import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

/**
 * 响应校验器
 * 
 * @author zc
 * @param <CK>
 *            校验器类型，用于自定义校验类
 */
public abstract class AbstractResponseChecker<CK extends AbstractResponseChecker> extends AbstractJsonChecker<CK> {

    public static final String CODE = "code";
    public static final String MESSAGE = "msg";
    public static final String BODY = "body";

    public AbstractResponseChecker(String response) {
        super(null);
        Assert.assertNotNull("响应不能为空", response);
        this.responseJson = JSON.parseObject(response);
        this.headJson = responseJson.getJSONObject("head");
        this.json = responseJson.getJSONObject("body");
        // Assert.assertNotNull("响应内容不能为空", bodyJson);
    }

    final JSONObject responseJson;
    final JSONObject headJson;

    public CK success() {
        codeMessage(GatewayReturnCode.SUCCESS, "apply success");
        return (CK)this;
    }

    public CK codeMessage(CodeMessageEnum code) {
        Assert.assertNotNull("head非空", headJson);
        Assert.assertEquals("返回码", code.getCode(), headJson.getString(CODE));
        Assert.assertEquals("返回信息", code.getMessage(), headJson.getString(MESSAGE));
        return (CK)this;
    }

    public CK codeMessage(CodeEnum code, String message) {
        Assert.assertNotNull("head非空", headJson);
        Assert.assertEquals("返回码", code.getCode(), headJson.getString(CODE));
        Assert.assertEquals("返回信息", message, headJson.getString(MESSAGE));
        return (CK)this;
    }

    public CK codeNoMessage(CodeEnum code) {
        Assert.assertNotNull("head非空", headJson);
        Assert.assertEquals("返回码", code.getCode(), headJson.getString(CODE));
        Assert.assertFalse("返回信息", headJson.containsKey(MESSAGE));
        return (CK)this;
    }

    public CK noBody() {
        Assert.assertFalse("body为空", responseJson.containsKey(BODY));
        return (CK)this;
    }

    public CK emptyBody() {
        Assert.assertTrue("body存在", responseJson.containsKey(BODY));
        Assert.assertTrue("body为empty", json.isEmpty());
        return (CK)this;
    }

    public CK body(String expectJsonString) {
        Assert.assertEquals(expectJsonString, json + "");
        return (CK)this;
    }

    public JSONObject getResponseJson() {
        return responseJson;
    }

    public JSONObject getHeadJson() {
        return headJson;
    }

    public JSONObject getBodyJson() {
        return json;
    }

}
