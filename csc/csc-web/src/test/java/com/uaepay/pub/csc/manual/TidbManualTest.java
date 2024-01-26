package com.uaepay.pub.csc.manual;

import javax.sql.DataSource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.DataSourceConfig;
import com.uaepay.pub.csc.domain.data.MySqlDataSourceConfig;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.test.base.ManualTestBase;

@Disabled
public class TidbManualTest extends ManualTestBase {

    @Autowired
    DataSourceConfigFactory dataSourceConfigFactory;

    @Test
    public void testQuerySource() {
        DataSource dataSource = null;// build1();
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate("tidbTest");
        if (dataSourceConfig instanceof MySqlDataSourceConfig) {
            dataSource = ((MySqlDataSourceConfig)dataSourceConfig).getDataSource();
        }

        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.setQueryTimeout(1000);
        String sql, relateField;

        sql = "select * from tidw.t_test_data limit 5";
        relateField = "order_no";
        SqlRowSet result = template.queryForRowSet(sql);
        ColumnData columnData = ColumnData.from(result.getMetaData(), relateField);
        SrcRows srcRows = new SrcRows(RowDataConverter.from(result, columnData));
        System.out.println(srcRows);
    }
}
