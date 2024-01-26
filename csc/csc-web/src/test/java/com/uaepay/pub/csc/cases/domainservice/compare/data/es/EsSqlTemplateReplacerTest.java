package com.uaepay.pub.csc.cases.domainservice.compare.data.es;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domainservice.data.es.EsSqlTemplateReplacer;

public class EsSqlTemplateReplacerTest {

    @Test
    public void testTemplateReplace_src() {
        Date beginTime = new DateTime(2020, 1, 1, 0, 0, 0).toDate();
        Date endTime = new DateTime(2020, 1, 2, 0, 0, 0).toDate();
        String template = "{\"query\": {\"bool\": {\"filter\": [{\"match\": {\"dataSet\": \"csc_unit_test\"}}"
            + ", {\"match\": {\"dataType\": \"S\"}}, {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}}}]}}"
            + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"]"
            + ", \"sort\": {\"orderNo.keyword\": \"asc\"}, \"from\": 0, \"size\": 2 }";
        String sql = new EsSqlTemplateReplacer(template).replaceDate(SqlTemplateParamEnum.BEGIN_TIME, beginTime)
            .replaceDate(SqlTemplateParamEnum.END_TIME, endTime).getSql();
        System.out.println(sql);
        String expectSql = "{\"query\": {\"bool\": {\"filter\": [{\"match\": {\"dataSet\": \"csc_unit_test\"}}"
            + ", {\"match\": {\"dataType\": \"S\"}}, {\"range\": {\"updateTime\": {\"gte\": \"2019-12-31T20:00:00Z\", \"lt\": \"2020-01-01T20:00:00Z\"}}}}]}}"
            + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"]"
            + ", \"sort\": {\"orderNo.keyword\": \"asc\"}, \"from\": 0, \"size\": 2 }";
        Assertions.assertEquals(expectSql, sql);
    }

    @Test
    public void testTemplateReplace_stringList() {
        String template = "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": {id_s}}}]}}}";
        checkReplaceStringList(Arrays.asList("a", "b", "c"),
            "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": [\"a\",\"b\",\"c\"]}}]}}}", template);
        checkReplaceStringList(Collections.emptyList(),
            "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": []}}]}}}", template);
        checkReplaceStringList(null, "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": []}}]}}}",
            template);
        checkReplaceStringList(Arrays.asList("", "   ", null),
            "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": []}}]}}}", template);
    }

    @Test
    public void testTemplateReplace_numberList() {
        String template = "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": {id_n}}}]}}}";
        checkReplaceNumberList(Arrays.asList("a", "b", "c"),
            "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": [a,b,c]}}]}}}", template);
        checkReplaceNumberList(Collections.emptyList(),
            "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": []}}]}}}", template);
        checkReplaceNumberList(null, "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": []}}]}}}",
            template);
        checkReplaceNumberList(Arrays.asList("", "   ", null),
            "{\"query\": {\"bool\": {\"filter\": [{\"terms\": {\"orderNo\": []}}]}}}", template);
    }

    private void checkReplaceStringList(List<String> idList, String expectSql, String template) {
        String sql =
            new EsSqlTemplateReplacer(template).replaceStringList(SqlTemplateParamEnum.ID_STRING, idList).getSql();
        System.out.println(sql);
        Assertions.assertEquals(expectSql, sql);
    }

    private void checkReplaceNumberList(List<String> idList, String expectSql, String template) {
        String sql =
            new EsSqlTemplateReplacer(template).replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, idList).getSql();
        System.out.println(sql);
        Assertions.assertEquals(expectSql, sql);
    }

}
