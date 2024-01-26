package com.uaepay.gateway.cgs.cases.provider.login;

import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;

@Ignore
public class LoginCheckProviderTest extends ApplicationTestBase {

    private static final String K_MOBILE = "mobile";
    private static final String K_VERIFY_TOKEN = "verifyToken";
    private static final String K_VERIFY_CODE = "verifyCode";

    private static final String K_ACCESS_TOKEN = "accessToken";
    private static final String K_ACCESS_KEY = "accessKey";

    private static final String MOCK_VERIFY_CODE = "888888";

    @Autowired
    private AccessTokenService accessTokenService;

    @Value("${login.verifyToken.allowCount:3}")
    private int maxAllowCount;

    @Test
    public void testBodyEmpty() {
        RequestBuilder builder = templateCheck().body("");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数错误: body").noBody();
    }

    @Test
    public void testMobileBlank() {
        RequestBuilder builder = templateCheck().param(K_MOBILE, null);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数错误: mobile").noBody();
    }

    @Test
    public void testVerifyTokenBlank() {
        RequestBuilder builder = templateCheck().param(K_VERIFY_TOKEN, null);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数错误: verifyToken").noBody();
    }

    @Test
    public void testVerifyCodeBlank() {
        RequestBuilder builder = templateCheck().param(K_VERIFY_TOKEN, "xxx").param(K_VERIFY_CODE, null);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数错误: verifyCode").noBody();
    }

    /**
     * 无token发起
     */
    @Test
    public void testTokenNotExist() {
        RequestBuilder builder = templateCheck().param(K_VERIFY_TOKEN, "xxx").param(K_VERIFY_CODE, "xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "验证信息已过期").noBody();
    }

    @Test
    public void testVerifySuccess() {
        RequestBuilder sendBuilder = LoginSendProviderTest.templateSend();
        ResponseChecker sendChecker = processRequest(sendBuilder);
        sendChecker.codeMessage(GatewayReturnCode.SUCCESS, "发送成功").paramNotBlank(K_VERIFY_TOKEN);

        String verifyToken = sendChecker.getStringParam(K_VERIFY_TOKEN);
        RequestBuilder checkBuilder =
            templateCheck().param(K_VERIFY_TOKEN, verifyToken).param(K_VERIFY_CODE, MOCK_VERIFY_CODE);
        ResponseChecker checkChecker = processRequest(checkBuilder);
        checkChecker.codeMessage(GatewayReturnCode.SUCCESS, "登录成功").paramNotBlank(K_ACCESS_TOKEN)
            .paramNotBlank(K_ACCESS_KEY);
    }

    @Test
    public void testVerifyFailToExpire() {
        RequestBuilder sendBuilder = LoginSendProviderTest.templateSend();
        ResponseChecker sendChecker = processRequest(sendBuilder);
        sendChecker.codeMessage(GatewayReturnCode.SUCCESS, "发送成功").paramNotBlank(K_VERIFY_TOKEN);

        String verifyToken = sendChecker.getStringParam(K_VERIFY_TOKEN);
        RequestBuilder checkBuilder = templateCheck().param(K_VERIFY_TOKEN, verifyToken).param(K_VERIFY_CODE, "xxx");
        ResponseChecker checkChecker;
        int allowCount = maxAllowCount;
        while (allowCount-- > 0) {
            checkChecker = processRequest(checkBuilder);
            checkChecker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "验证码错误").noBody();
        }

        checkChecker = processRequest(checkBuilder);
        checkChecker.codeMessage(GatewayReturnCode.BIZ_CHECK_FAIL, "验证信息已过期").noBody();
    }

    /**
     * 登录成功，覆盖已登录信息
     */
    @Test
//    @Ignore
    public void testVerifySuccessOverride() {
        RequestBuilder sendBuilder = LoginSendProviderTest.templateSend();
        ResponseChecker sendChecker1 = processRequest(sendBuilder);
        sendChecker1.codeMessage(GatewayReturnCode.SUCCESS, "发送成功").paramNotBlank(K_VERIFY_TOKEN);
        ResponseChecker sendChecker2 = processRequest(sendBuilder);
        sendChecker2.codeMessage(GatewayReturnCode.SUCCESS, "发送成功").paramNotBlank(K_VERIFY_TOKEN);

        RequestBuilder checkBuilder1 = templateCheck()
            .param(K_VERIFY_TOKEN, sendChecker1.getStringParam(K_VERIFY_TOKEN)).param(K_VERIFY_CODE, MOCK_VERIFY_CODE);
        ResponseChecker checkChecker1 = processRequest(checkBuilder1);
        checkChecker1.codeMessage(GatewayReturnCode.SUCCESS, "登录成功").paramNotBlank(K_ACCESS_TOKEN)
            .paramNotBlank(K_ACCESS_KEY);
        Assert.assertNotNull(accessTokenService.getAccessToken(checkChecker1.getStringParam(K_ACCESS_TOKEN)));

        RequestBuilder checkBuilder2 = templateCheck()
            .param(K_VERIFY_TOKEN, sendChecker2.getStringParam(K_VERIFY_TOKEN)).param(K_VERIFY_CODE, MOCK_VERIFY_CODE);
        ResponseChecker checkChecker2 = processRequest(checkBuilder2);
        checkChecker2.codeMessage(GatewayReturnCode.SUCCESS, "登录成功").paramNotBlank(K_ACCESS_TOKEN)
            .paramNotBlank(K_ACCESS_KEY);
        Assert.assertNull(accessTokenService.getAccessToken(checkChecker1.getStringParam(K_ACCESS_TOKEN)));
        Assert.assertNotNull(accessTokenService.getAccessToken(checkChecker2.getStringParam(K_ACCESS_TOKEN)));
    }

    public static RequestBuilder templateCheck() {
        return new RequestBuilder("/login/checkVerifyCode").template().param(K_MOBILE, LoginSendProviderTest.V_MOBILE);
    }

}
