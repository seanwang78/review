package com.uaepay.gateway.cgs.app.template.testtool;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import com.uaepay.gateway.cgs.app.template.testtool.common.RequestBuilder;
import com.uaepay.gateway.cgs.app.template.testtool.common.ResponseChecker;
import com.uaepay.gateway.cgs.app.template.testtool.test.base.ApplicationTestBase;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

public class HelloProviderTest extends ApplicationTestBase {

    FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZZ");

    @Test
    public void testInvalidName() {
        RequestBuilder request = RequestBuilder.template("/hello");

        ResponseChecker response = doService(request);
        response.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: name").noBody();
    }

    @Test
    public void testInvalidRequestTime() {
        RequestBuilder request = RequestBuilder.template("/hello").param("name", "xxx");
        ResponseChecker response = doService(request);
        response.codeMessage(GatewayReturnCode.INVALID_PARAMETER, "invalid parameter: requestTime").noBody();
    }

    @Test
    public void testSuccess() throws ParseException {
        String requestTime = "2019-10-02T10:30:50+04:00";
        RequestBuilder request =
            RequestBuilder.template("/hello").param("name", "中国").param("requestTime", requestTime);
        ResponseChecker response = doService(request);
        String expectRequestTime = DATE_FORMAT.parse(requestTime).getTime() + "";// DATE_FORMAT_WITH_MILLIS.format();
        response.success().codeMessage(GatewayReturnCode.SUCCESS, "apply success").param("greet", "hello 中国")
            .param("requestTime", expectRequestTime);
    }

}
