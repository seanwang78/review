package com.uaepay.pub.csc.cases.domainservice.monitor.data.es;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.pub.csc.cases.util.FileUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIteratorFactory;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class EsQueryDataIteratorTest extends MockTestBase {

    @Autowired
    QueryDataIteratorFactory queryDataIteratorFactory;

    @Autowired
    TestDataMocker testDataMocker;

    QueryRows queryRows;
    List<RowData> rows;

    @Test
    public void testQuery() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-03-01 02:30:00");
        testDataMocker.monitorDataBuilder("3", "E", "0.03").updateTime("2020-03-01 03:30:00");
        testDataMocker.monitorDataBuilder("4", "E", "4.00").updateTime("2020-03-01 04:30:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_alarm_es_query_error_1.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(3, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals(
            "_id=ut_ma_status_M_2_E, amount=0.00020, orderNo=2, updateTime=2020-02-29T22:30:00.000Z, status=E",
            rows.get(0).toString());
        Assertions.assertEquals(
            "_id=ut_ma_status_M_3_E, amount=0.03, orderNo=3, updateTime=2020-02-29T23:30:00.000Z, status=E",
            rows.get(1).toString());
        Assertions.assertEquals(
            "_id=ut_ma_status_M_4_E, amount=4.0, orderNo=4, updateTime=2020-03-01T00:30:00.000Z, status=E",
            rows.get(2).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testQuery_withSplit() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("1", "S", "0.0001").updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.0002").updateTime("2020-03-01 02:30:00");
        testDataMocker.monitorDataBuilder("3", "E", "0.03").updateTime("2020-03-01 12:30:00");
        testDataMocker.monitorDataBuilder("4", "E", "4.00").updateTime("2020-03-01 14:30:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_alarm_es_query_error_1.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        // 12小时分页
        MonitorDefine define =
            new MonitorDefineBuilder("test").es().alarm(template).split(SplitStrategyEnum.UNION, 720).build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(1, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals(
            "_id=ut_ma_status_M_2_E, amount=0.00020, orderNo=2, updateTime=2020-02-29T22:30:00.000Z, status=E",
            rows.get(0).toString());

        // 分页2
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        Assertions.assertEquals(2, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals(
            "_id=ut_ma_status_M_3_E, amount=0.03, orderNo=3, updateTime=2020-03-01T08:30:00.000Z, status=E",
            rows.get(0).toString());
        Assertions.assertEquals(
            "_id=ut_ma_status_M_4_E, amount=4.0, orderNo=4, updateTime=2020-03-01T10:30:00.000Z, status=E",
            rows.get(1).toString());

        // 分页3
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testAgg_1() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "2", "E", "0.0002")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district2", "3", "E", "3")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district2", "4", "E", "4")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district2", "5", "E", "5")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_1.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(2, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals(
            "group1=province1, group2=city2, group3=district2, _count=3, max=5.0, min=3.0, maxDate=2020-02-29T21:30:00.000Z",
            rows.get(0).toString());
        Assertions.assertEquals(
            "group1=province1, group2=city1, group3=district1, _count=1, max=0.00020, min=0.00020, maxDate=2020-02-29T21:30:00.000Z",
            rows.get(1).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testAgg_2() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district4", "8", "E", "9")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district5", "9", "E", "10")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district5", "10", "E", "11")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "11", "E", "12")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "12", "E", "13")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "13", "E", "14")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city2", "district6", "14", "E", "15")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_1.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(3, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals(
            "group1=province1, group2=city2, group3=district6, _count=4, max=15.0, min=12.0, maxDate=2020-02-29T21:30:00.000Z",
            rows.get(0).toString());
        Assertions.assertEquals(
            "group1=province1, group2=city2, group3=district5, _count=2, max=11.0, min=10.0, maxDate=2020-02-29T21:30:00.000Z",
            rows.get(1).toString());
        Assertions.assertEquals(
            "group1=province1, group2=city1, group3=district4, _count=1, max=9.0, min=9.0, maxDate=2020-02-29T21:30:00.000Z",
            rows.get(2).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    /** 一层聚合 */
    @Test
    public void testAgg_depth1() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "S", "0.0001")
            .updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "2", "E", "3")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "3", "E", "4")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "4", "E", "5")
            .updateTime("2020-03-01 01:30:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_3.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(1, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals("max=5.0, min=3.0, maxDate=2020-02-29T21:30:00.000Z, count=3", rows.get(0).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    /** 一层聚合 */
    @Test
    public void testAgg_withFormat() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("province1", "city1", "district1", "1", "E", "0.50")
            .updateTime("2020-03-01 00:30:00");
        testDataMocker.monitorDataBuilder("province1", "city1", "district2", "3", "E", "6.00")
            .updateTime("2020-03-01 00:45:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_alarm_es_aggregation_5_format.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(1, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals("max=6.0, min=0.5, maxDate=02-29 20:45+00, count=2", rows.get(0).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testAgg_normal_1() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("1", "S", "1.00").group1("g1").updateTime("2020-03-01 11:00:00");
        testDataMocker.monitorDataBuilder("2", "E", "2.00").group1("g1").updateTime("2020-03-01 12:00:00");
        testDataMocker.monitorDataBuilder("3", "E", "3.00").group1("g2").updateTime("2020-03-01 13:00:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_aggregation_normal_1.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(3, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals("status=E, group1=g1, _count=1, maxAmount=2.0, maxDate=2020-03-01T08:00:00.000Z",
            rows.get(0).toString());
        Assertions.assertEquals("status=E, group1=g2, _count=1, maxAmount=3.0, maxDate=2020-03-01T09:00:00.000Z",
            rows.get(1).toString());
        Assertions.assertEquals("status=S, group1=g1, _count=1, maxAmount=1.0, maxDate=2020-03-01T07:00:00.000Z",
            rows.get(2).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testAgg_normal_bucketSelector() {
        testDataMocker.reset("ut_ma_status");
        testDataMocker.monitorDataBuilder("1", "S", "0.01").updateTime("2020-03-01 11:00:00");
        testDataMocker.monitorDataBuilder("2", "E", "0.02").updateTime("2020-03-01 12:00:00");
        testDataMocker.monitorDataBuilder("3", "E", "0.03").updateTime("2020-03-01 13:00:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_aggregation_normal_2_bucket_selector.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(1, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals("status=E, _count=2, maxDate=03-01 09:00+00", rows.get(0).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testAgg_error_1_invalidAggParams() {
        String template = FileUtil.readToString("es_test/monitor_aggregation_error_1_empty_agg.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        FailException ex = Assertions.assertThrows(FailException.class, queryDataIterator::next);
        Assertions.assertEquals("BAD_SQL", ex.getCode());
        Assertions.assertEquals("invalid aggregation params", ex.getMessage());
    }

    @Test
    public void testAgg_error_2_unknown_agg_function() {
        String template = FileUtil.readToString("es_test/monitor_aggregation_error_2_unknown_agg_function.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        InvalidParameterException ex =
            Assertions.assertThrows(InvalidParameterException.class, queryDataIterator::next);
        Assertions.assertEquals("不支持的聚合类型: bucket_selector2", ex.getMessage());
    }

    @Test
    public void testAgg_filter() {
        testDataMocker.reset("ut_agg_filter");
        testDataMocker.monitorDataBuilder("a", "S", "0.01").updateTime("2020-03-01 11:00:00");
        testDataMocker.monitorDataBuilder("a", "E", "0.02").updateTime("2020-03-01 12:00:00");
        testDataMocker.monitorDataBuilder("c", "E", "0.03").updateTime("2020-03-01 13:00:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_aggregation_normal_3_filter.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(1, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals("dataType=M, _count=3, success=1", rows.get(0).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

    @Test
    public void testAgg_bucketScript_bucketSort() {
        testDataMocker.reset("ut_agg_script");
        testDataMocker.monitorDataBuilder("A", "a1", "S", "0.01").updateTime("2020-03-01 11:00:00");
        testDataMocker.monitorDataBuilder("A", "a2", "E", "0.02").updateTime("2020-03-01 12:00:00");
        testDataMocker.monitorDataBuilder("A", "a3", "S", "0.03").updateTime("2020-03-01 12:00:00");
        testDataMocker.monitorDataBuilder("B", "b1", "E", "0.03").updateTime("2020-03-01 13:00:00");
        testDataMocker.mockEs();

        String template = FileUtil.readToString("es_test/monitor_aggregation_normal_4_script.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2020, 3, 1, 0, 0, 0).toDate())
            .endTime(new DateTime(2020, 3, 2, 0, 0, 0).toDate());
        MonitorDefine define = new MonitorDefineBuilder("test").es().alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        System.out.println(queryRows);
        Assertions.assertEquals(2, queryRows.size());
        rows = queryRows.getRows();
        Assertions.assertEquals("dataType=B, _count=1, success=0, success_ratio=0.00%", rows.get(0).toString());
        Assertions.assertEquals("dataType=A, _count=3, success=2, success_ratio=66.67%", rows.get(1).toString());

        // 分页2
        Assertions.assertFalse(queryDataIterator.hasNext());
    }

}
