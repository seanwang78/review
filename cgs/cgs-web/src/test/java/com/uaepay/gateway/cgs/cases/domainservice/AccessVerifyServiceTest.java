package com.uaepay.gateway.cgs.cases.domainservice;

import com.uaepay.gateway.cgs.domainservice.AccessVerifyService;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.common.app.template.common.exception.GatewayFailException;
import com.uaepay.pts.ext.facade.response.AccessTokenResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/11/11
 * @since 0.1
 */
public class AccessVerifyServiceTest extends ApplicationTestBase {


    @Autowired
    private AccessVerifyService accessVerifyService;

    @Test
    public void test_verifyToken_notExists() {
        try {
            AccessTokenResponse response = accessVerifyService.verifyToken("unknown_access_token");
        } catch (GatewayFailException e) {
            Assert.assertEquals("401", e.getCode());
            return ;
        }
        Assert.assertTrue("错误的逻辑",false);
    }
    @Test
    public void test_verifyToken_exists() {
        AccessTokenResponse response = accessVerifyService.verifyToken("40478a8b26814580ac4de4de5329e370");
        Assert.assertTrue(response.isSuccess());
        Assert.assertNotNull(response.getAccessToken());
        Assert.assertNotNull(response.getAccessKey());
    }
}
