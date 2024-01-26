package com.uaepay.gateway.cgs.cases.provider.test;

import org.junit.Test;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class TestMockLoginProviderTest extends ApplicationTestBase {

    @Test
    public void testSuccess() {
        RequestBuilder request = new RequestBuilder("/test/mockLogin").template().param("mobile", "xxx");
        ResponseChecker checker = processRequest(request);
        checker.codeMessage(GatewayReturnCode.SUCCESS, "apply success").paramNotBlank("accessToken").paramNotBlank("accessKey");
    }

}
