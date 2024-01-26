package com.uaepay.pub.csc.cases.util;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.core.common.util.CscUtil;

public class CscUtilTest {

    @Test
    public void testSplitString() {
        checkSplitString("a,b,c", "[a, b, c]");
        checkSplitString("a，b，c", "[a, b, c]");
        checkSplitString("a|b|c", "[a, b, c]");
        checkSplitString(null, "[]");
        checkSplitString("", "[]");
        checkSplitString("   ", "[]");
        // 去掉空白字符
        checkSplitString(",,,a,,b,,,c,,,", "[a, b, c]");
        // trim
        checkSplitString("    a   ,   b  ,   c   ", "[a, b, c]");
        // 复杂测试
        checkSplitString(" ,,   a , , ,   b  ， ， ，， ，  c ||||| | d   ", "[a, b, c, d]");
    }

    private void checkSplitString(String splitString, String expectToString) {
        List<String> result = CscUtil.splitString(splitString);
        Assertions.assertEquals(expectToString, result.toString());
    }

}
