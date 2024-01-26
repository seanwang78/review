package com.uaepay.gateway.cgs.cases.provider.test;

import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import org.junit.Test;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/9/21
 * @since 0.1
 */
public class TestConditionProviderTest extends ApplicationTestBase {


    @Test
    public void test_noTokenPass() {
        RequestBuilder builder = new RequestBuilder("/test/condition").template().platform(PlatformType.H5.getCode());
        builder.param("name", "world");

        ResponseChecker checker = processRequest(builder);
        checker.success();
    }
}
