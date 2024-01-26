package com.uaepay.gateway.cgs.app.template.testtool.common;

import org.junit.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zc
 */
public class JsonArrayChecker {

    public JsonArrayChecker(JSONArray json) {
        this.json = json;
    }

    private final JSONArray json;

    public JsonArrayChecker size(int count) {
        Assert.assertEquals(count, json.size());
        return this;
    }

    public JsonArrayChecker param(int index, String value) {
        Assert.assertEquals(value, json.getString(index));
        return this;
    }

    public JsonArrayChecker param(int index, int value) {
        Assert.assertEquals(value, json.getIntValue(index));
        return this;
    }

    public JSONObject getJsonObject(int index) {
        return json.getJSONObject(index);
    }

    public JsonChecker jsonChecker(int index) {
        return new JsonChecker(json.getJSONObject(index));
    }

}
