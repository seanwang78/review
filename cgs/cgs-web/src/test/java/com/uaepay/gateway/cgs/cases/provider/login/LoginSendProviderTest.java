package com.uaepay.gateway.cgs.cases.provider.login;

import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.junit.Ignore;
import org.junit.Test;

import com.uaepay.gateway.cgs.app.service.provider.login.enums.SendType;
import com.uaepay.gateway.cgs.app.service.provider.login.enums.VerifyScene;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;

@Ignore
public class LoginSendProviderTest extends ApplicationTestBase {

    private static final String K_MOBILE = "mobile";
    private static final String K_SEND_TYPE = "sendType";
    private static final String K_SCENE = "scene";
    private static final String K_VERIFY_TOKEN = "verifyToken";

    public static final String V_MOBILE = "+86 123456";

    @Test
    public void testBodyEmpty() {
        RequestBuilder builder = templateSend().body("");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数错误: body").noBody();
    }

    @Test
    public void testMobileBlank() {
        RequestBuilder builder = templateSend().param(K_MOBILE, null);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数错误: mobile").noBody();
    }

    @Test
    public void testSendTypeError() {
        RequestBuilder builder = templateSend().param(K_SEND_TYPE, "xxx");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数值错误: xxx").noBody();
    }

    @Test
    public void testSceneError() {
        RequestBuilder builder = templateSend().param(K_SCENE, "yyy");
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "参数值错误: yyy").noBody();
    }

    @Test
    public void testSendSuccess() {
        RequestBuilder builder = templateSend();
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, "发送成功").paramNotBlank(K_VERIFY_TOKEN);
    }

    public static RequestBuilder templateSend() {
        return new RequestBuilder("/login/sendVerifyCode").template().param(K_MOBILE, V_MOBILE)
            .param(K_SEND_TYPE, SendType.SMS.name()).param(K_SCENE, VerifyScene.LOGIN.name());
    }

}
