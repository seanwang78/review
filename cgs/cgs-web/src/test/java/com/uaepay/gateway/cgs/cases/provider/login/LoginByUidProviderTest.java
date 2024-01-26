package com.uaepay.gateway.cgs.cases.provider.login;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class LoginByUidProviderTest extends ApplicationTestBase {

    private static final String K_ACCESS_TOKEN = "accessToken";
    private static final String K_ACCESS_KEY = "accessKey";

    @Test
    public void testLoginByUidSuccess() {
        RequestBuilder builder = templateCheck().param("uid", "971118421933840333");
        ResponseChecker checker = processRequest(builder);

        checker.codeMessage(GatewayReturnCode.SUCCESS, "登录成功").paramNotBlank(K_ACCESS_TOKEN)
                .paramNotBlank(K_ACCESS_KEY);
    }

    public static RequestBuilder templateCheck() {
        return new RequestBuilder("/login/loginByUid").template();
    }

}
