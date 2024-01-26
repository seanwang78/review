package com.uaepay.gateway.cgs.cases.ext.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.gateway.cgs.integration.grc.GrcClient;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.grc.connect.api.vo.domain.OtherEventInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * grc client test
 * </p>
 *
 * @author lidewen
 * @version GrcClientTest.java, V0.1 2022/6/13 2:13 下午 lidewen Exp $
 */
@Slf4j
public class GrcClientTest extends ApplicationTestBase {

    @Autowired
    private GrcClient grcClient;

    @Test
    public void testQueryByBiz() {
        String eventId = "ccd6c91899614a149dd3c97aca2d755c";
        OtherEventInfo info = grcClient.querySingleOtherEvent(eventId, null);
        Assert.assertEquals(info.getEventType(), "USERNAME_PASSWORD_LOGIN");
    }

    @Test
    public void testQueryByTicket() {
        String ticket = "f8029e5f4ca14c5fa560a20be7913ea3";
        OtherEventInfo info = grcClient.querySingleOtherEvent(null, ticket);
        Assert.assertEquals(info.getEventType(), "USERNAME_PASSWORD_LOGIN");
    }
}
