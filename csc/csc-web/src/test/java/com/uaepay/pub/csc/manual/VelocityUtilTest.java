package com.uaepay.pub.csc.manual;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.uaepay.common.util.VelocityUtil;
import com.uaepay.validate.exception.ValidationException;

@Disabled
public class VelocityUtilTest {

    @Test
    public void test() throws ValidationException {
        Map<String, Object> params = new HashMap<>();
//        params.put("check", new DataCheckUtil());

        // String exp =
        // "#if($!check.notCorrespond('S1', 'S1,S2', 'S', 'S') and $!check.notCorrespond('F', 'F1,F2', 'F', 'F2'))Status
        // not correspond#end";
        String exp =
            "#if ($!check.notCorrespond('S', 'S1,S2', 'F', 'S') and $!check.notCorrespond('S', 'F1,F2', 'F', 'F2'))"
                + "Status not correspond" + "#end";
        String result = VelocityUtil.getString(exp, params);
        System.out.println("result: " + result);
    }

}
