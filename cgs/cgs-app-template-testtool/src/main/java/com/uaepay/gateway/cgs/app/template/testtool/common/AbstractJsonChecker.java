package com.uaepay.gateway.cgs.app.template.testtool.common;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 抽象JSON校验器
 * 
 * @author zc
 */
public abstract class AbstractJsonChecker<CK extends AbstractJsonChecker> {

    public AbstractJsonChecker(JSONObject json) {
        this.json = json;
    }

    protected JSONObject json;

    public CK paramCount(int count) {
        Assert.assertEquals(count, json.keySet().size());
        return (CK)this;
    }

    public CK param(String name, String value) {
        Assert.assertEquals("业务参数: " + name, value, json.getString(name));
        return (CK)this;
    }

    public CK paramBoolean(String name, boolean value) {
        Assert.assertEquals("业务参数: " + name, value, json.getBooleanValue(name));
        return (CK)this;
    }

    public CK paramNotBlank(String name) {
        Assert.assertTrue("业务参数非空: " + name, StringUtils.isNotBlank(json.getString(name)));
        return (CK)this;
    }

    public CK paramStartsWith(String name, String prefix) {
        paramNotBlank(name);
        Assert.assertTrue("业务参数前缀错误: " + name, json.getString(name).startsWith(prefix));
        return (CK)this;
    }

    public CK paramMoneyInfo(String name, String amount, String currency) {
        JsonChecker jsonChecker = jsonChecker(name);
        jsonChecker.paramCount(2).param("amount", amount).param("currency", currency);
        return (CK)this;
    }

    public CK paramNull(String name) {
        Assert.assertTrue(json.containsKey(name));
        Assert.assertNull(json.get(name));
        return (CK)this;
    }

    public String getStringParam(String name) {
        return json.getString(name);
    }

    public JSONObject getJsonObject(String paramName) {
        return json.getJSONObject(paramName);
    }

    public JsonChecker jsonChecker(String paramName) {
        return new JsonChecker(json.getJSONObject(paramName));
    }

    public JSONArray getJsonArray(String name) {
        return json.getJSONArray(name);
    }

    public JsonArrayChecker arrayChecker(String name) {
        return new JsonArrayChecker(json.getJSONArray(name));
    }
}
