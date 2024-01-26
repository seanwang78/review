package com.uaepay.pub.csc.manual;

import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domainservice.data.JdbcTemplateFactory;
import com.uaepay.pub.csc.test.base.ManualTestBase;

@Disabled
public class JdbcTemplateFactoryTest extends ManualTestBase {

    @Autowired
    private JdbcTemplateFactory jdbcTemplateFactory;

    @Test
    public void testQuerySource() {
        BasicDataSource dataSource = build1();

        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.setQueryTimeout(1000);

        String sql, relateField;

        // sql =
        // "select t.voucher_no, t.market_status, t.amount currency_amount, cast(trim(both '\"' from
        // t.extension->\"$.gpAmount\") as decimal(19,2)) gp_amount\n"
        // + " , cast(trim(both '\"' from t.extension->\"$.gpRate\") as unsigned) gp_rate, t.update_time\n"
        // + "from tradeii.t_trade_marketing t left join tradeii.t_refund_order r on t.voucher_no = r.refund_voucher_no
        // \n"
        // + "where t.market_method_type = 'GP'\n" + " and t.order_type = 'R'\n"
        // + " and t.market_status in ('RS')\n" + " and r.refund_type = 'REFUND'\n"
        // + " and t.update_time >= now() - interval 2 day\n" + " -- and t.update_time >= {begin}\n"
        // + " -- and t.update_time < {end}\n" + "order by trade_marketing_id";
        // relateField = "voucher_no";

        sql =
            "select t.transaction_id, t.request_no, t.voucher_service, tad.accounting_type, tad.amount, tao.currency_amount, tao.exchange_rate, t.create_time\n"
                + "from gpoint.t_accounting_transaction t\n"
                + "  left join gpoint.t_accounting_detail tad on t.transaction_id = tad.transaction_id\n"
                + "  left join gpoint.t_accept_order tao on t.transaction_id = tao.transaction_id \n"
                + "where tao.exchange_rate is not null order by t.transaction_id desc limit 5";
        relateField = "request_no";

        SqlRowSet result = template.queryForRowSet(sql);
        ColumnData columnData = ColumnData.from(result.getMetaData(), relateField);
        SrcRows srcRows = new SrcRows(RowDataConverter.from(result, columnData));
        System.out.println(srcRows);
    }

    private BasicDataSource build1() {
        BasicDataSource result = new BasicDataSource();
        result.setUrl("jdbc:mysql://dbdev1.g42paytest.com:3306/basis?useSSL=false");
        result.setUsername("reader");
        result.setPassword("reader_j0kqgaHxsI5O6j");
        result.setDriverClassName("com.mysql.jdbc.Driver");
        result.setMaxTotal(10);
        result.setMaxIdle(5);
        result.setMinIdle(1);
        result.setTestOnBorrow(true);
        return result;
    }

    private BasicDataSource build2() throws Exception {
        Properties properties = new Properties();
        BasicDataSource result = BasicDataSourceFactory.createDataSource(properties);
        return result;
    }

}
