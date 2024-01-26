package com.uaepay.gateway.cgs.cases.app.facade;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.common.app.template.common.util.GatewayJsonUtil;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

import lombok.Data;

public class ClientServiceResponseJsonTest {

    @Data
    public static class Body {
        String field1;
    }

    @Test
    public void test() {
        Map<String, Object> extParams = new HashMap<>();
        extParams.put("a", "1");

        Body body = new Body();
        body.setField1("test field1");

        ClientServiceResponse<Body> response =
            ClientServiceResponse.build(GatewayReturnCode.SUCCESS, "test message", extParams, body);

        Assert.assertEquals(
            "{\"head\":{\"code\":\"200\",\"msg\":\"test message\"},\"body\":{\"field1\":\"test field1\"}}",
            GatewayJsonUtil.toJsonStringMute(response));
    }

}
