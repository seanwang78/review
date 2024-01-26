package com.uaepay.gateway.cgs.cases.domainservice;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.ues.UesClientV2;
import com.uaepay.ues.ctx.EncryptContextV2;
import com.uaepay.ues.ctx.params.Temporarily;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UesClientTest extends ApplicationTestBase {

    @Autowired
    UesClientV2 uesClient;

    @Test
    public void testTempData() throws Exception {
        String plain = "123";

        EncryptContextV2 context = new EncryptContextV2();
        context.setPlainData(plain);
        Temporarily temporarily = new Temporarily();
        temporarily.setTimeout(60);
        context.inTemporarily(temporarily);
        boolean result = uesClient.saveData(context);
        System.out.printf("result: %s, ticket: %s\n", result, context.getTicket());
        Assert.assertTrue(result);
        Assert.assertNotNull(context.getTicket());

        EncryptContextV2 context2 = new EncryptContextV2();
        context2.setTicket(context.getTicket());
        result = uesClient.getDataByTicket(context2);
        System.out.printf("result: %s, ticket: %s\n", result, context2.getPlainData());
        Assert.assertTrue(result);
        Assert.assertEquals(plain, context2.getPlainData());
    }

    @Test
    public void testTempDataExpired() throws Exception {
        String plain = "123456";

        EncryptContextV2 context = new EncryptContextV2();
        context.setPlainData(plain);
        Temporarily temporarily = new Temporarily();
        temporarily.setTimeout(2);
        context.inTemporarily(temporarily);
        boolean result = uesClient.saveData(context);
        System.out.printf("result: %s, ticket: %s\n", result, context.getTicket());
        Assert.assertTrue(result);
        Assert.assertNotNull(context.getTicket());

        Thread.sleep(5000);

        EncryptContextV2 context2 = new EncryptContextV2();
        context2.setTicket(context.getTicket());
        result = uesClient.getDataByTicket(context2);
        System.out.printf("result: %s, ticket: %s\n", result, context2.getPlainData());
        Assert.assertFalse(result);
    }

}
