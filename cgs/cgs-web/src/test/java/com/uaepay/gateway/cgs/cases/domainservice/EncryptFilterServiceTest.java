package com.uaepay.gateway.cgs.cases.domainservice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilter;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptFilterFactory;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import com.uaepay.ues.ctx.params.Temporarily;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

public class EncryptFilterServiceTest extends ApplicationTestBase {

    @Autowired
    EncryptFilterFactory encryptFilterFactory;

    @Autowired
    UesClientV2 uesClient;

    /**
     * 单层，多字段
     */
    @Test
    public void testSingleLayer() {
        JSONObject body = new JSONObject();
        body.put("password", "123");
        body.put("idCard", "456");
        body.put("address", "789");

        String encrypt = "password:MOCK, idCard:MOCK";
        String expectBody = "{\"password\":\"123changed\",\"address\":\"789\",\"idCard\":\"456changed\"}";

        assertFilter(body, encrypt, expectBody);
    }

    /**
     * 多策略
     */
    @Test
    public void testMultiStrategy() {
        JSONObject body = new JSONObject();
        body.put("password", "123");
        body.put("idCard", "456");
        body.put("address", "789");

        String encrypt = "password:MOCK, idCard:MOCK2";
        String expectBody = "{\"password\":\"123changed\",\"address\":\"789\",\"idCard\":\"456changed2\"}";

        assertFilter(body, encrypt, expectBody);
    }

    /**
     * 多层，多字段
     */
    @Test
    public void testMultiLayer() {
        JSONObject body = new JSONObject();
        JSONObject personal = new JSONObject();
        personal.put("name", "123");
        personal.put("idNo", "456");
        personal.put("address", "789");
        body.put("personal", personal);

        String encrypt = "personal.name:MOCK, personal.idNo:MOCK";
        String expectBody = "{\"personal\":{\"address\":\"789\",\"name\":\"123changed\",\"idNo\":\"456changed\"}}";

        assertFilter(body, encrypt, expectBody);
    }

    /**
     * 数组类型
     */
    @Test
    public void testArrayType() {
        JSONObject body = new JSONObject();
        JSONArray cards = new JSONArray();
        JSONObject card1 = new JSONObject();
        card1.put("cardNo", "123");
        card1.put("bankCode", "ABC");
        JSONObject card2 = new JSONObject();
        card2.put("cardNo", "456");
        card2.put("bankCode", "CMB");
        cards.add(card1);
        cards.add(card2);
        body.put("cards", cards);

        String encrypt = "cards.cardNo:MOCK";
        String expectBody =
            "{\"cards\":[{\"bankCode\":\"ABC\",\"cardNo\":\"123changed\"},{\"bankCode\":\"CMB\",\"cardNo\":\"456changed\"}]}";

        assertFilter(body, encrypt, expectBody);
    }




    @Test
    public void testResponseSingleLayer() {
        JSONObject body = new JSONObject();
        body.put("password", "123");
        body.put("idCard", encryptUesTemp("12345"));
        body.put("address", "789");

        String encrypt = "idCard:UES";
        String expectBody = "{\"password\":\"123\",\"address\":\"789\",\"idCard\":\"12345\"}";

        assertFilter(body, encrypt, expectBody);
    }



    private void assertFilter(JSONObject body, String encrypt, String expectBody) {
        EncryptFilter filter = encryptFilterFactory.create(encrypt);
        String origBody = body.toJSONString();
        System.out.println("orig: " + origBody);
        filter.filterReplace(body, null);
        String newBody = body.toJSONString();
        System.out.println("new : " + newBody);
        Assert.assertEquals(expectBody, newBody);
    }

    private String encryptUesTemp(String plain) {
        EncryptContextV2 context = new EncryptContextV2();
        context.setPlainData(plain);
        Temporarily temporarily = new Temporarily();
        temporarily.setTimeout(60);
        context.inTemporarily(temporarily);
        try {
            boolean success = uesClient.saveData(context);
            if (!success) {
                throw new ErrorException("加密异常: " + context.getResultCode() + ", " + context.getResultMessage());
            }
        } catch (InvocationTargetException e) {
            throw new ErrorException("加密异常: 调用异常");
        } catch (Exception e) {
            throw new ErrorException("加密异常: 调用异常");
        }
        return context.getTicket();
    }

}
