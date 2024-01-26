package com.uaepay.gateway.cgs.cases.provider.test;

import org.junit.Test;

import com.uaepay.basis.beacon.common.util.RsaUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class TestEncryptProviderTest extends ApplicationTestBase {

    private static final String K_PASSWORD = "password";
    private static final String K_UES_PASSWORD = "uesPassword";

    /**
     * 空字段不加密
     */
    @Test
    public void testNullValueNotEncrypt() {
        MockAccess accessInfo = mockAccess();
        RequestBuilder builder = template().accessInfo(accessInfo);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, "apply success").emptyBody();
    }

    /**
     * 字段非空，盐不存在
     */
    @Test
    public void testSaltNotExist() {
        MockAccess accessInfo = mockAccess();
        RequestBuilder builder = template().accessInfo(accessInfo).param(K_PASSWORD, "xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "The network is busy. Please try again.").noBody();
    }

    /**
     * 字段非空，盐不存在
     */
    @Test
    public void testDecryptFail() {
        MockAccess accessInfo = mockAccess();
        mockSalt(accessInfo.getAccessToken());
        RequestBuilder builder = template().accessInfo(accessInfo).param(K_PASSWORD, "xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "parameter decrypt failed: password").noBody();
    }

    /**
     * 明文不包含盐
     */
    @Test
    public void testPlainWithoutSalt() throws Exception {
        MockAccess accessInfo = mockAccess();
        mockSalt(accessInfo.getAccessToken());
        RequestBuilder builder = template().accessInfo(accessInfo).param(K_PASSWORD,
            RsaUtil.encrypt("xxx", GatewayConstants.DEFAULT_CHARSET, UAT_PUBLIC_KEY_1, 2048));
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "plain not with salt: password").noBody();
    }

    /**
     * 加密成功
     */
    @Test
    public void testSuccess() throws Exception {
        MockAccess accessInfo = mockAccess();
        String salt = mockSalt(accessInfo.getAccessToken());
        RequestBuilder builder = template().accessInfo(accessInfo).param(K_PASSWORD,
            RsaUtil.encrypt("xxx" + salt, GatewayConstants.DEFAULT_CHARSET, UAT_PUBLIC_KEY_1, 2048));
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, "apply success").paramStartsWith(K_UES_PASSWORD, "C");
    }

    /**
     * 加密成功，同时支持多密钥
     */
    @Test
    public void testSuccess2() throws Exception {
        MockAccess accessInfo = mockAccess();
        String salt = mockSalt(accessInfo.getAccessToken());
        RequestBuilder builder = template().accessInfo(accessInfo).param(K_PASSWORD,
            RsaUtil.encrypt("xxx" + salt, GatewayConstants.DEFAULT_CHARSET, UAT_PUBLIC_KEY_2, 2048));
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, "apply success").paramStartsWith(K_UES_PASSWORD, "C");
    }

    public static RequestBuilder template() {
        return new RequestBuilder("/test/encrypt").template();
    }

}
