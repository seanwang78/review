package com.uaepay.pub.csc.cases.domainservice.compare.data.es;

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

public class EsSrcDataIteratorTest extends MockTestBase {

    @Autowired
    private SrcDataIteratorFactory srcDataIteratorFactory;

    @Autowired
    private TestDataMocker testDataMocker;

    /**
     * 测试分页
     */
    @Test
    public void test_query_page() {
        testDataMocker.reset("unittest_success");
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.6789").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.mockEs();

        String template = "{\"index\": \"i_dev_csc_test_data\""
            + ", \"query\": { \"bool\": { \"filter\": [ {\"match\": {\"dataSet\": \"unittest_success\"}}"
            + ", {\"match\": {\"dataType\": \"S\"}}"
            + ", {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}} ]}}"
            + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"], \"sort\": {\"updateTime\": \"asc\"} }";
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2019, 10, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2019, 10, 2, 0, 0, 0).toDate());
        // 切分时间：24小时
        SrcDataIterator srcDataIterator =
            srcDataIteratorFactory.create("es_business_reader", template, 1440, "orderNo", queryParam);

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
        // 可优化，精度问题
        Assertions.assertEquals(new BigDecimal("0.00010"), rows.get(0).getItem("amount"));
        rows = groupMap.get("b").getRows();
        Assertions.assertEquals("F", rows.get(0).getItem("status"));
        // 可优化，精度丢失
        Assertions.assertEquals(new BigDecimal("123456789012345.67"), rows.get(0).getItem("amount"));

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
        Assertions.assertEquals(new BigDecimal("0.01"), rows.get(0).getItem("amount"));

        // 切分1，分页终止
        Assertions.assertFalse(srcDataIterator.hasNext());
    }

}
