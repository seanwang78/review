package com.uaepay.gateway.cgs.cases.provider.basic;

import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/1/15
 * @since 0.1
 */
public class CheckLogonProviderTest extends ApplicationTestBase {

    @Autowired
    private AccessMocker accessMocker;

    @Test
    public void test_check() {
        MockAccess access = accessMocker.mock(new MockAccess());
        RequestBuilder builder = template().accessInfo(access);
        ResponseChecker checker = processRequest(builder);
        checker.codeMessage(GatewayReturnCode.SUCCESS, null);
    }


    public static RequestBuilder template() {
        return new RequestBuilder("/common/checkLogon").template();
    }

}
