package com.uaepay.pub.csc.cases.domainservice.compare.data.mongo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domainservice.data.mongo.MongoSqlTemplateReplacer;

public class MongoSqlTemplateReplacerTest {

    @Test
    public void testTemplateReplace_src() {
        Date beginTime = new DateTime(2020, 1, 1, 0, 0, 0).toDate();
        Date endTime = new DateTime(2020, 1, 2, 0, 0, 0).toDate();
        String template = "{find: 'csc_test_data', filter: {$and: [{dataSet: 'csc_unit_test'}"
            + ", {updateTime: {$gte: {begin}, $lt: {end}}}]}"
            + ", projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}, sort: {updateTime: 1}}";

        String sql = new MongoSqlTemplateReplacer(template).replaceDate(SqlTemplateParamEnum.BEGIN_TIME, beginTime)
            .replaceDate(SqlTemplateParamEnum.END_TIME, endTime).getSql();
        System.out.println(sql);

        String expectSql = "{find: 'csc_test_data', filter: {$and: [{dataSet: 'csc_unit_test'}"
            + ", {updateTime: {$gte: ISODate('2019-12-31T20:00:00Z'), $lt: ISODate('2020-01-01T20:00:00Z')}}]}"
            + ", projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}, sort: {updateTime: 1}}";
        Assertions.assertEquals(expectSql, sql);
    }

    @Test
    public void testTemplateReplace_stringList() {
        String template = "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: {id_s}}}]}}";
        checkReplaceStringList(Arrays.asList("a", "b", "c"),
            "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: ['a','b','c']}}]}}", template);
        checkReplaceStringList(Collections.emptyList(),
            "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: []}}]}}", template);
        checkReplaceStringList(null, "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: []}}]}}", template);
        checkReplaceStringList(Arrays.asList("", "   ", null),
            "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: []}}]}}", template);
    }

    @Test
    public void testTemplateReplace_numberList() {
        String template = "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: {id_n}}}]}}";
        checkReplaceNumberList(Arrays.asList("a", "b", "c"),
            "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: [a,b,c]}}]}}", template);
        checkReplaceNumberList(Collections.emptyList(),
            "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: []}}]}}", template);
        checkReplaceNumberList(null, "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: []}}]}}", template);
        checkReplaceNumberList(Arrays.asList("", "   ", null),
            "{find: 'csc_test_data', filter: {$and: [{order_no: {$in: []}}]}}", template);
    }

    private void checkReplaceStringList(List<String> idList, String expectSql, String template) {
        String sql =
            new MongoSqlTemplateReplacer(template).replaceStringList(SqlTemplateParamEnum.ID_STRING, idList).getSql();
        System.out.println(sql);
        Assertions.assertEquals(expectSql, sql);
    }

    private void checkReplaceNumberList(List<String> idList, String expectSql, String template) {
        String sql =
            new MongoSqlTemplateReplacer(template).replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, idList).getSql();
        System.out.println(sql);
        Assertions.assertEquals(expectSql, sql);
    }

}
