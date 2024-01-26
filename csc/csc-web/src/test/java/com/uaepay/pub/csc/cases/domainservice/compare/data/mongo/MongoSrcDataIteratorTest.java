package com.uaepay.pub.csc.cases.domainservice.compare.data.mongo;

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

public class MongoSrcDataIteratorTest extends MockTestBase {

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
        testDataMocker.mockMongo();

        String template =
            "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: 'unittest_success'}, {dataType: 'S'}"
                + ", {updateTime: {$gte: {begin}, $lt: {end}}}"
                + "]}, projection: {_id: 0, orderNo: 1, status: 1, amount: 1}, sort: {updateTime: 1}}";
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2019, 10, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2019, 10, 2, 0, 0, 0).toDate());
        // 切分时间：24小时
        SrcDataIterator srcDataIterator =
            srcDataIteratorFactory.create("mongo_reader", template, 1440, "orderNo", queryParam);

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
        Assertions.assertEquals(new BigDecimal("0.0001"), rows.get(0).getItem("amount"));
        rows = groupMap.get("b").getRows();
        Assertions.assertEquals("F", rows.get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("123456789012345.1234"), rows.get(0).getItem("amount"));

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

    @Test
    public void testAggregate() {
        testDataMocker.reset("unittest_success");
        testDataMocker.srcDataBuilder("a", "F", "0.0001").updateTime("2019-10-01 01:00:00");
        testDataMocker.srcDataBuilder("b", "S", "0.01").updateTime("2019-10-01 02:30:00");
        testDataMocker.srcDataBuilder("c", "S", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.srcDataBuilder("d", "E", "0.01").updateTime("2019-10-01 03:00:00");
        testDataMocker.mockMongo();

        String template = "{db: 'testdb', aggregate: 'csc_test_data', pipeline: ["
            + "{$match: {$and: [{dataSet: 'unittest_success'}, {updateTime: {$gte: {begin}, $lt: {end}}}]}}"
            + ", {$group: {_id: '$status', totalAmount: {$sum: '$amount'}}}" + ", {$sort: {_id: 1}}" + "]}";
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2019, 10, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2019, 10, 2, 0, 0, 0).toDate());
        // 切分时间：24小时
        SrcDataIterator srcDataIterator =
            srcDataIteratorFactory.create("mongo_reader", template, 1440, "_id", queryParam);

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
        Assertions.assertEquals("E", relateValues.get(0));
        Assertions.assertEquals("F", relateValues.get(1));

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(2, groupMap.size());

        rows = groupMap.get("E").getRows();
        Assertions.assertEquals(new BigDecimal("0.01"), rows.get(0).getItem("totalAmount"));
        rows = groupMap.get("F").getRows();
        Assertions.assertEquals(new BigDecimal("0.0001"), rows.get(0).getItem("totalAmount"));

        // 切分1，分页2
        Assertions.assertTrue(srcDataIterator.hasNext());
        srcRows = srcDataIterator.next();
        System.out.println(srcRows);

        relateValues = srcRows.getRelateValues();
        Assertions.assertEquals(1, relateValues.size());
        Assertions.assertEquals("S", relateValues.get(0));

        groupMap = srcRows.getGroupMap();
        Assertions.assertEquals(1, groupMap.size());

        rows = groupMap.get("S").getRows();
        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals(new BigDecimal("0.02"), rows.get(0).getItem("totalAmount"));

        // 切分1，分页终止
        Assertions.assertFalse(srcDataIterator.hasNext());
    }

}
