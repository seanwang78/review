package com.uaepay.pub.csc.manual;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.pagehelper.Page;
import com.github.pagehelper.dialect.helper.MySqlDialect;

@Disabled
public class PageHelperTest {

    Pattern PATTERN = Pattern.compile("\\$\\{\\.*}");

    @Test
    public void test() {
        MySqlDialect dialect = new MySqlDialect();
        String sql = dialect.getPageSql(
            "select 1 from payment.t_payment_order where gmt_modified >= ${begin} and gmt_modified < ${end} order by payment_voucher_no",
            new Page(2, 100), null);
        System.out.println(sql);
    }

    @Test
    public void test2() {
        String sql =
            "select 1 from payment.t_payment_order where gmt_modified >= ${begin} and gmt_modified < ${end} order by payment_voucher_no";
        PATTERN.matcher(sql).matches();
    }

}
