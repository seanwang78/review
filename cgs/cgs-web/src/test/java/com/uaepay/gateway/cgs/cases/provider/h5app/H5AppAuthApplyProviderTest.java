package com.uaepay.gateway.cgs.cases.provider.h5app;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class H5AppAuthApplyProviderTest extends ApplicationTestBase {

    private static final String OK_DOMAIN_1 = "test.domain";
    private static final String OK_DOMAIN_2 = "ok.domain";

    @Autowired
    AccessMocker accessMocker;

    @Test
    public void testInvalidParameter() {
        MockAccess access = accessMocker.mock(new MockAccess());
        RequestBuilder builder;
        ResponseChecker checker;

        builder = applyTemplate(null, "xxx").accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: codeChallenge").noBody();

        builder = applyTemplate("xxx", null).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: appDomain").noBody();
    }

    @Test
    public void testAppDomainNotAllowed() {
        MockAccess access = accessMocker.mock(new MockAccess());
        RequestBuilder builder = applyTemplate("xxx", "xxx.domain").accessInfo(access);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "Domain not allowed!").noBody();
    }

    @Test
    public void testApplySuccess() {
        MockAccess access = accessMocker.mock(new MockAccess());
        RequestBuilder builder;
        ResponseChecker checker;

        builder = applyTemplate("xxx", OK_DOMAIN_1).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");

        builder = applyTemplate("xxx", OK_DOMAIN_2).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");

        builder = applyTemplate("xxx", "https://ok.domain/abc?a=1").accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");
    }

    public static RequestBuilder applyTemplate(String codeChallenge, String appDomain) {
        return new RequestBuilder("/h5app/auth/apply").template().param("codeChallenge", codeChallenge)
            .param("appDomain", appDomain);
    }

}
