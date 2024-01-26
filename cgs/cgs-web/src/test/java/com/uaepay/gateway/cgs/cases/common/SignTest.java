package com.uaepay.gateway.cgs.cases.common;

import com.uaepay.basis.beacon.common.util.ShaUtil;
import com.uaepay.gateway.cgs.constants.GatewayConstants;
import org.junit.Test;

public class SignTest {

    @Test
    public void test() {
        String sign = ShaUtil.getSha256("{}8b6d80df41de4cef9a473bfd0d24ba2a", GatewayConstants.DEFAULT_CHARSET);
        System.out.println(sign);

    }

}
