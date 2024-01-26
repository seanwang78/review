package com.uaepay.gateway.cgs.cases.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.uaepay.basis.beacon.common.util.ShaUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import lombok.Data;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class JsonTest {

    String json =
        "{\"platform\":\"1\",\"version\":\"1.0.0\",\"service\":\"Login.SendVerifyCode\",\"lang\":\"EN\",\"biz_content\":{\"mobile\":\"+86 13512345678\",\"send_type\":\"SMS\",\"scene\":\"LOGIN\",\"desc\":\"啦啦啦\",\"list\":[{\"key\":\"a\",\"value\":\"1\"},{\"key\":\"b\",\"value\":\"2\"}]}}";

    @Test
    public void test1() {
        JSONObject jsonObject = JSON.parseObject(json);
        System.out.println(jsonObject);

        JSONObject bizContent = (JSONObject)jsonObject.get("biz_content");
        JSONArray list = (JSONArray)bizContent.get("list");
        Iterator<Object> iter = list.iterator();
        while (iter.hasNext()) {
            JSONObject item = (JSONObject)iter.next();
            item.put("value", item.get("value") + "changed");
        }
        System.out.println(jsonObject);
    }

    @Test
    public void test2() {
        JSONObject jsonObject = JSON.parseObject(json);
        System.out.println(jsonObject);

        JSONObject bizContent = (JSONObject)jsonObject.get("biz_content");
        System.out.println(bizContent.toString());
    }

    @Test
    public void test() {
        System.out.println(ShaUtil.getSha256(
            "{\"appId\":\"PAYBY_1.0\",\"bizId\":\"rSTWJ7PThy1602901832\"}70f1724b732947e6b770b7345cab6cb6",
            GatewayConstants.DEFAULT_CHARSET));
    }

    @Data
    public static class ItemRequest {
        List<Item> items;
    }

    @Data
    @JSONType(seeAlso = {ItemA.class, ItemB.class}, typeKey = "type")
    public static class Item {

        @JSONField(ordinal = -1)
        String type;

    }

    @Data
    @JSONType(typeName = "A")
    public static class ItemA extends Item {
        String password;
    }

    @Data
    @JSONType(typeName = "B")
    public static class ItemB extends Item {
        String face;
        String la;
    }

    @Test
    public void testInherent() {
        String orig = "{'items': [{'type':'A', 'password':'123'}, {'type':'B', 'face':'abc'}]}";
        System.out.println("orig        : " + orig);
        ItemRequest request = JSON.parseObject(orig, ItemRequest.class);
        System.out.println("json parsed : " + request);
        String orig2 = JSON.toJSONString(request);
        System.out.println("toString    : " + orig2);
        request = JSON.parseObject(orig2, ItemRequest.class);
        System.out.println("parse again : " + request);

        JSONObject obj = JSON.parseObject(orig);
        System.out.println("json object : " + JSON.toJSONString(obj));
    }

}
