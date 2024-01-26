package com.uaepay.gateway.cgs.cases.provider.test;

import org.junit.Test;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class TestUnauthedProviderTest extends ApplicationTestBase {

    @Test
    public void testBodyError() {
        RequestBuilder builder = new RequestBuilder("/test/unauthed").template();
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: body").noBody();
    }

    @Test
    public void testNormal() {
        RequestBuilder builder = new RequestBuilder("/test/unauthed").template();
        builder.param("name", "world");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, "apply success").param("greet", "hello world").paramNotBlank("count");
    }

}
