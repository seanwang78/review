package com.uaepay.gateway.cgs.app.template.testtool.test.common;

import java.util.List;

import com.uaepay.gateway.cgs.app.template.testtool.common.JsonArrayChecker;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.uaepay.gateway.cgs.app.template.testtool.common.JsonChecker;

import lombok.AllArgsConstructor;
import lombok.Data;

public class JsonCheckerTest {

    @Test
    public void test() {
        // DomainA a = new DomainA();
        // a.setFieldA("aaa");
        // a.setDomainList2(new ArrayList<>());
        // a.getDomainList2().add(new DomainB("abc", "123"));
        // a.getDomainList2().add(new DomainB("def", "456"));
        // String jsonA = JSON.toJSONString(a);
        String jsonA =
            "{\"fieldA\":\"aaa\",\"stringList\":null,\"domainList\":[{\"field1\":\"abc\",\"field2\":\"123\"},{\"field1\":\"def\",\"field2\":\"456\"}]}";
        System.out.println(jsonA);

        JsonChecker checker = new JsonChecker(JSON.parseObject(jsonA));
        checker.paramCount(3);
        checker.param("fieldA", "aaa");
        checker.paramNull("stringList");
        JsonArrayChecker arrayChecker = checker.arrayChecker("domainList");
        arrayChecker.size(2);
        arrayChecker.jsonChecker(0).param("field1", "abc").param("field2", "123");
        arrayChecker.jsonChecker(1).param("field1", "def").param("field2", "456");

    }

    @Data
    public static class DomainA {

        String fieldA;

        List<String> stringList;

        List<DomainB> domainList;
    }

    @Data
    @AllArgsConstructor
    public static class DomainB {
        String field1;

        String field2;
    }

}
