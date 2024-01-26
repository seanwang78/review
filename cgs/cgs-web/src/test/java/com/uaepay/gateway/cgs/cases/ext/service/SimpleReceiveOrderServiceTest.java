package com.uaepay.gateway.cgs.cases.ext.service;

import com.google.common.collect.Lists;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.ext.service.impl.ReceiveOrderServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/4/19
 * @since 0.1
 */
public class SimpleReceiveOrderServiceTest {

    ReceiveOrderServiceImpl service;
    ReceiveOrderContext context;
    Header header;
    @Before
    public void before() {
        service = new ReceiveOrderServiceImpl();
        context = new ReceiveOrderContext(null, null, false, null);
        header = Header.newHeader();
        context.setHeader(header);
        service.hostApps = Lists.newArrayList("totok-pay", "payby", "botim-pay","cashnow");
        service.defaultHostApp = "totok-pay";
        service.checkHostAppMinVersion = "1.0.9";
    }

    /** 测试非法sdkVersion */
    @Test
    public void test_unknown_sdkVersions() {
        header.setSdkVersion(null);
        service.checkHeader(context);
        Assert.assertEquals(service.defaultHostApp, header.getHostApp());

        // clean
        header.setHostApp(null);

        header.setSdkVersion("0.1");
        service.checkHeader(context);
        Assert.assertEquals(service.defaultHostApp, header.getHostApp());
    }

    /** 小于检查版本，但是hostApp非法 */
    @Test
    public void test_less_than_checkVersion_hostAppError() {
        header.setSdkVersion("1.0.8");
        header.setHostApp("3.000");
        service.checkHeader(context);
        Assert.assertEquals(service.defaultHostApp, header.getHostApp());
    }

    /** 小于检查版本，但是hostApp正常 */
    @Test
    public void test_less_than_checkVersion_hostAppNormal() {
        header.setSdkVersion("1.0.8");
        header.setHostApp("payby");
        service.checkHeader(context);
        Assert.assertNotEquals(service.defaultHostApp, header.getHostApp());
        Assert.assertEquals("payby", header.getHostApp());
    }

    /** 大于检查版本，但是hostApp非法或者hostAppVersion为空 */
    @Test(expected = InvalidParameterException.class)
    public void test_great_than_checkVersion() {
        header.setSdkVersion("1.0.10");
        header.setHostApp("payby");
        header.setHostAppversion(null);
        service.checkHeader(context);
    }
}