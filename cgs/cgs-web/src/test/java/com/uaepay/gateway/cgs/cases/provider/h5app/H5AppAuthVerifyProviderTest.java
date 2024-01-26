package com.uaepay.gateway.cgs.cases.provider.h5app;

import com.uaepay.gateway.cgs.cases.common.CacheTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.uaepay.basis.beacon.common.util.ShaUtil;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class H5AppAuthVerifyProviderTest extends ApplicationTestBase {

    private static final String OK_DOMAIN_1 = "test.domain";
    private static final String OK_DOMAIN_2 = "https://test.domain/xxx?p=1";

    @Autowired
    AccessMocker accessMocker;

    @Autowired
    CacheTool cacheTool;

    @Test
    public void testInvalidParameter() {
        RequestBuilder builder;
        ResponseChecker checker;

        builder = verifyTemplate(null, "xxx");
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: vt").noBody();

        builder = verifyTemplate("xxx", null);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: cv").noBody();
    }

    @Test
    public void testDomainNotAllowed() {
        RequestBuilder builder = verifyTemplate("xxx", "xxx").referer(null);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "Domain not allowed!").noBody();
    }

    @Test
    public void testTokenNotExist() {
        RequestBuilder builder = verifyTemplate("xxx", "xxx").referer(OK_DOMAIN_1);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "verification expired").noBody();
    }

    @Test
    public void testCodeNotMatch() {
        RequestBuilder builder;
        ResponseChecker checker;

        MockAccess access = accessMocker.mock(new MockAccess());
        builder = H5AppAuthApplyProviderTest.applyTemplate("xxx", OK_DOMAIN_1).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");
        String verifyToken = checker.getStringParam("verifyToken");

        accessMocker.clear();
        builder = verifyTemplate(verifyToken, "xxx").referer(OK_DOMAIN_1);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "wrong code").noBody();

        builder = verifyTemplate(verifyToken, "xxx").referer(OK_DOMAIN_1);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "wrong code").noBody();

        // 两次之后就过期
        builder = verifyTemplate(verifyToken, "xxx").referer(OK_DOMAIN_1);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "verification expired").noBody();
    }

    @Test
    public void testVerifySuccessWithNoCookie() {
        RequestBuilder builder;
        ResponseChecker checker;
        String plain = RandomStringUtils.randomAscii(64);

        MockAccess access = accessMocker.mock(new MockAccess());
        builder = H5AppAuthApplyProviderTest.applyTemplate(ShaUtil.getSha256(plain), OK_DOMAIN_1).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");
        String verifyToken = checker.getStringParam("verifyToken");

        builder = verifyTemplate(verifyToken, plain).referer(OK_DOMAIN_1);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).noBody();

        builder = saltTemplate();
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.UNAUTHORIZED, "common.unauthorized").noBody();
    }

    @Test
    public void testVerifySuccess() {
        RequestBuilder builder;
        ResponseChecker checker;
        String plain = RandomStringUtils.randomAscii(64);

        MockAccess access = accessMocker.mock(new MockAccess());
        builder = H5AppAuthApplyProviderTest.applyTemplate(ShaUtil.getSha256(plain), OK_DOMAIN_2).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");
        String verifyToken = checker.getStringParam("verifyToken");

        TestRestTemplate testRestTemplate = new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
        builder = verifyTemplate(verifyToken, plain).referer(OK_DOMAIN_2);
        checker = processRequest(builder, testRestTemplate);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null);

        builder = saltTemplate();
        checker = processRequest(builder, testRestTemplate);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("salt");
    }

    @Test
    public void testVerifySuccess_miniAppBotimPay() {
        cacheTool.evictAll();

        RequestBuilder builder;
        ResponseChecker checker;
        String plain = RandomStringUtils.randomAscii(64);

        MockAccess access = accessMocker.mock(new MockAccess());
        builder = H5AppAuthApplyProviderTest.applyTemplate(ShaUtil.getSha256(plain), OK_DOMAIN_2).accessInfo(access);
        checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("verifyToken");
        String verifyToken = checker.getStringParam("verifyToken");

        TestRestTemplate testRestTemplate = new TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
        builder = verifyTemplate(verifyToken, plain).referer(OK_DOMAIN_2);
        checker = processRequest(builder, testRestTemplate);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null);

        builder = saltTemplate();
        builder.platform("6");
        builder.accessToken(access.getAccessToken());
        builder.ucid(access.getResponse().getIdentity());
        checker = processRequest(builder, testRestTemplate);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank("salt");
    }

    public static RequestBuilder verifyTemplate(String verifyToken, String codeVerifier) {
        return new RequestBuilder("/h5app/auth/v2/verify").template().platform(PlatformType.H5.getCode())
            .param("verifyToken", verifyToken).param("codeVerifier", codeVerifier);
    }

    public static RequestBuilder saltTemplate() {
        return new RequestBuilder("/common/getSalt").platform(PlatformType.H5.getCode()).template();
    }

}
