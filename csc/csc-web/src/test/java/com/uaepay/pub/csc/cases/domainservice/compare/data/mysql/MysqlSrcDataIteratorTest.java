package com.uaepay.pub.csc.cases.domainservice.compare.data.mysql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.domain.compare.GroupRows;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIteratorFactory;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MysqlSrcDataIteratorTest extends MockTestBase {

    @Autowired
    private SrcDataIteratorFactory srcDataIteratorFactory;

    @Autowired
    private TestDataMocker testDataMocker;

    /**
     * 测试分页
     */
    @Test
    public void test_page() {
        testDataMocker.reset("unittest_success");
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.1234").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.mock();

        String template = "select order_no, status, amount TRADE_AMOUNT, update_time"
            + " from csc.t_test_data where data_set = 'unittest_success' and data_type = 'S'"
            + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}";
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2019, 10, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2019, 10, 2, 0, 0, 0).toDate());
        // 切分时间：24小时
        SrcDataIterator srcDataIterator =
            srcDataIteratorFactory.create("default", template, 1440, "order_no", queryParam);

        SrcRows srcRows;
        List<String> relateValues;
        Map<String, GroupRows> groupMap;
        List<RowData> rows;
        // 切分1，分页1
        Assertions.assertTrue(srcDataIterator.hasNext());
        srcRows = srcDataIterator.next();
        System.out.println(srcRows);

        relateValues = srcRows.getRelateValues();
        Assertions.assertEquals(2, relateValues.size());
        Assertions.assertEquals("a", relateValues.get(0));
        Assertions.assertEquals("b", relateValues.get(1));

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(2, groupMap.size());

        rows = groupMap.get("a").getRows();
        Assertions.assertEquals("S", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.0001"), rows.get(0).getItem("trade_amount"));
        rows = groupMap.get("b").getRows();
        Assertions.assertEquals("F", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("123456789012345.1234"), rows.get(0).getItem("trade_amount"));

        // 切分1，分页2
        Assertions.assertTrue(srcDataIterator.hasNext());
        srcRows = srcDataIterator.next();
        System.out.println(srcRows);

        relateValues = srcRows.getRelateValues();
        Assertions.assertEquals(1, relateValues.size());
        Assertions.assertEquals("c", relateValues.get(0));

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(1, groupMap.size());

        rows = groupMap.get("c").getRows();
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("F", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.0100"), rows.get(0).getItem("trade_amount"));

        // 切分1，分页终止
        Assertions.assertFalse(srcDataIterator.hasNext());
    }

    /**
     * 测试时间分片
     */
    @Test
    public void test_time_split() throws Exception {
        testDataMocker.reset("unittest_success");
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.1234").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2019-10-01 02:40:00");
        testDataMocker.mock();

        String template = "select order_no, status, amount, update_time"
            + " from csc.t_test_data where data_set = 'unittest_success' and data_type = 'S'"
            + " and update_time >= {begin} and update_time < {end} order by order_no limit {offset}, {count}";
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2019, 10, 1, 1, 0, 0).toDate())
            .endTime(new DateTime(2019, 10, 1, 3, 0, 0).toDate());
        // 切分时间：60分钟
        SrcDataIterator srcDataIterator =
            srcDataIteratorFactory.create("default", template, 60, "order_no", queryParam);

        SrcRows srcRows;
        List<String> relateValues;
        Map<String, GroupRows> groupMap;
        List<RowData> rows;
        // 切分1，分页1
        Assertions.assertTrue(srcDataIterator.hasNext());
        srcRows = srcDataIterator.next();
        System.out.println(srcRows);

        relateValues = srcRows.getRelateValues();
        Assertions.assertEquals(1, relateValues.size());
        Assertions.assertEquals("a", relateValues.get(0));

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(1, groupMap.size());

        rows = groupMap.get("a").getRows();
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("S", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.0001"), rows.get(0).getItem("amount"));

        // 切分2，分页1
        Assertions.assertTrue(srcDataIterator.hasNext());
        srcRows = srcDataIterator.next();
        System.out.println(srcRows);

        relateValues = srcRows.getRelateValues();
        Assertions.assertEquals(2, relateValues.size());
        Assertions.assertEquals("b", relateValues.get(0));
        Assertions.assertEquals("c", relateValues.get(1));

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(2, groupMap.size());

        rows = groupMap.get("b").getRows();
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("F", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("123456789012345.1234"), rows.get(0).getItem("amount"));

        rows = groupMap.get("c").getRows();
        Assertions.assertEquals("F", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.0100"), rows.get(0).getItem("amount"));

        // 切分2，分页终止
        Assertions.assertTrue(srcDataIterator.hasNext());
        srcRows = srcDataIterator.next();
        System.out.println(srcRows);

        relateValues = srcRows.getRelateValues();
        Assertions.assertEquals(0, relateValues.size());

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(0, groupMap.size());
    }

}
