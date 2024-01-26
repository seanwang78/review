package com.uaepay.gateway.cgs.cases.provider.basic;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class CommonGetSaltProviderTest extends ApplicationTestBase {

    private static final String K_SALT = "salt";

    @Autowired
    AccessMocker accessMocker;

    @Test
    public void testSignBlank() {
        RequestBuilder builder = template();
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: sign").noBody();
    }

    @Test
    public void testAccessTokenBlank() {
        RequestBuilder builder = template().sign("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: Access-Token").noBody();
    }

    @Test
    public void testAccessTokenNotExist() {
        RequestBuilder builder = template().accessToken("xxx").sign("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_TOKEN, "invalid token").noBody();
    }

    @Test
    public void testAccessTokenSqueezeOut() {
        MockAccess access = accessMocker.mock(new MockAccess().squeezeOut());
        RequestBuilder builder = template().accessToken(access.getAccessToken()).sign("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SQUEEZE_OUT, "New login to another device").noBody();
    }

    @Test
    public void testAccessTokenDeviceIdNotMatch() {
        MockAccess access = accessMocker.mock(new MockAccess().deviceId("d1"));
        RequestBuilder builder = template().accessToken(access.getAccessToken()).sign("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.UNAUTHORIZED,
            "Current login device may be can't match with authorized device").noBody();
    }

    @Test
    public void testSignMismatch() {
        MockAccess access = accessMocker.mock(new MockAccess().deviceId("d1"));
        RequestBuilder builder = template().accessToken(access.getAccessToken()).deviceId("d1").sign("xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.UNAUTHORIZED, "common.unauthorized").noBody();
    }

    @Test
    public void testMemberPermissionReject() {
        MockAccess access = accessMocker.mock(new MockAccess().memberPermissionCheck(false));
        RequestBuilder builder = template().accessInfo(access);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.RISK_FAIL,
            "Process is failed. Please retry or contact ToPay customer service.").noBody();
    }

    @Test
    public void testSuccess() {
        MockAccess access = accessMocker.mock(new MockAccess());
        RequestBuilder builder = template().accessInfo(access);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null).paramNotBlank(K_SALT);
    }

    public static RequestBuilder template() {
        return new RequestBuilder("/common/getSalt").template();
    }

}
