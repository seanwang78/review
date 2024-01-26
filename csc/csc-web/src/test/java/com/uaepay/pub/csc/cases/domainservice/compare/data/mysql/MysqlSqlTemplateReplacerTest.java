package com.uaepay.pub.csc.cases.domainservice.compare.data.mysql;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domainservice.data.mysql.MysqlSqlTemplateReplacer;

public class MysqlSqlTemplateReplacerTest {

    @Test
    public void testMysqlTemplateReplace_src() {
        Date beginTime = new DateTime(2019, 12, 15, 16, 10, 0).toDate();
        Date endTime = new DateTime(2019, 12, 15, 16, 20, 0).toDate();
        int offset = 1000;
        int count = 200;
        String template =
            "select bpo.PAYMENT_ORDER_NO, bpo.BIZ_PAYMENT_TYPE, bpo.BIZ_PAYMENT_STATE, bpo.BIZ_SUB_STATE\n"
                + "from payment.tb_biz_payment_order bpo\n"
                + "where bpo.GMT_MODIFIED >= {begin} and bpo.GMT_MODIFIED < {end}\n"
                + "order by bpo.PAYMENT_ORDER_NO limit {offset}, {count}";

        String sql = new MysqlSqlTemplateReplacer(template).replaceDate(SqlTemplateParamEnum.BEGIN_TIME, beginTime)
            .replaceDate(SqlTemplateParamEnum.END_TIME, endTime).replaceNumber(SqlTemplateParamEnum.OFFSET, offset)
            .replaceNumber(SqlTemplateParamEnum.COUNT, count).getSql();
        System.out.println(sql);

        String expectSql =
            "select bpo.PAYMENT_ORDER_NO, bpo.BIZ_PAYMENT_TYPE, bpo.BIZ_PAYMENT_STATE, bpo.BIZ_SUB_STATE\n"
                + "from payment.tb_biz_payment_order bpo\n"
                + "where bpo.GMT_MODIFIED >= str_to_date('2019-12-15 16:10:00', '%Y-%m-%d %H:%i:%s') and bpo.GMT_MODIFIED < str_to_date('2019-12-15 16:20:00', '%Y-%m-%d %H:%i:%s')\n"
                + "order by bpo.PAYMENT_ORDER_NO limit 1000, 200";
        Assertions.assertEquals(expectSql, sql);
    }

    @Test
    public void testMysqlTemplateReplace_stringList() {
        String template = "select 1 from order_no in {id_s}";
        checkReplaceStringList(Arrays.asList("a", "b", "c"), "select 1 from order_no in ('a','b','c')", template);
        // checkReplaceStringList(Arrays.asList(new String[] {1, 2, 3}), "select 1 from order_no in ('1','2','3')",
        // template);
        checkReplaceStringList(Collections.emptyList(), "select 1 from order_no in (null)", template);
        checkReplaceStringList(null, "select 1 from order_no in (null)", template);
        checkReplaceStringList(Arrays.asList("", "   ", null), "select 1 from order_no in (null)", template);
    }

    @Test
    public void testMysqlTemplateReplace_numberList() {
        String template = "select 1 from order_no in {id_n}";
        checkReplaceNumberList(Arrays.asList("a", "b", "c"), "select 1 from order_no in (a,b,c)", template);
        // checkReplaceNumberList(Arrays.asList(new String[] {1, 2, 3}), "select 1 from order_no in (1,2,3)",
        // template);
        checkReplaceNumberList(Collections.emptyList(), "select 1 from order_no in (null)", template);
        checkReplaceNumberList(null, "select 1 from order_no in (null)", template);
        checkReplaceNumberList(Arrays.asList("", "   ", null), "select 1 from order_no in (null)", template);
    }

    private void checkReplaceStringList(List<String> idList, String expectSql, String template) {
        String sql =
            new MysqlSqlTemplateReplacer(template).replaceStringList(SqlTemplateParamEnum.ID_STRING, idList).getSql();
        System.out.println(sql);
        Assertions.assertEquals(expectSql, sql);
    }

    private void checkReplaceNumberList(List<String> idList, String expectSql, String template) {
        String sql =
            new MysqlSqlTemplateReplacer(template).replaceNumberList(SqlTemplateParamEnum.ID_NUMBER, idList).getSql();
        System.out.println(sql);
        Assertions.assertEquals(expectSql, sql);
    }

}
