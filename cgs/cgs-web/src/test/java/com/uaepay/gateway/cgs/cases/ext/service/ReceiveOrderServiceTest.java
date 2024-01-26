package com.uaepay.gateway.cgs.cases.ext.service;

import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;

public class ReceiveOrderServiceTest extends ApplicationTestBase {

    /**
     * http get不支持
     */
    @Test
    public void testGetDisabled() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(url, null, String.class);
        Assert.assertEquals(405, response.getStatusCodeValue());
        Assert.assertNull(response.getBody());
    }

    /**
     * 语言错误
     */
    @Test
    public void testLanguageNotSupported() {
        RequestBuilder builder = new RequestBuilder();
        builder.lang("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "language not supported").noBody();
    }

    /**
     * 服务为空
     */
    @Test
    public void testServiceBlank() {
        RequestBuilder builder = new RequestBuilder().template();
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SERVICE_NOT_AVAILABLE, "service not available").noBody();
    }

    /**
     * 服务为空，默认语言英语
     */
    @Test
    public void testServiceBlankDefaultLanguage() {
        RequestBuilder builder = new RequestBuilder().template();
        builder.lang(null);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SERVICE_NOT_AVAILABLE, "service not available").noBody();
    }

    @Test
    public void testPlatformTypeError() {
        RequestBuilder builder = new RequestBuilder("/whatever").template();
        builder.platform("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: platform").noBody();
    }

    /**
     * body不是json格式
     */
    @Test
    public void testJsonBodyFormatError() {
        RequestBuilder builder = new RequestBuilder("/whatever").template();
        builder.body("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "json format error").noBody();
    }

    /**
     * 服务不存在
     */
    @Test
    public void testServiceNotAvailable() {
        RequestBuilder builder = new RequestBuilder("/whatever").template();
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SERVICE_NOT_AVAILABLE, "service not available").noBody();
    }

}
