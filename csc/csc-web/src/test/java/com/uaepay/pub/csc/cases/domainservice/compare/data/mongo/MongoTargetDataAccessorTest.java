package com.uaepay.pub.csc.cases.domainservice.compare.data.mongo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.domain.compare.GroupRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessorFactory;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MongoTargetDataAccessorTest extends MockTestBase {

    @Autowired
    private TargetDataAccessorFactory targetDataAccessorFactory;

    @Autowired
    private TestDataMocker testDataMocker;

    @Test
    public void test_find() {
        testDataMocker.reset("unittest_success");
        testDataMocker.targetDataBuilder("1", "S", "0.0001");
        testDataMocker.targetDataBuilder("1", "SS", "0.0002");
        testDataMocker.targetDataBuilder("2", "Fail", "123456789012345.1234");
        testDataMocker.targetDataBuilder("3", "Failure", "0.01");
        testDataMocker.mockMongo();

        String template =
            "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: 'unittest_success'}, {dataType: 'T'}, {orderNo:{$in:{id_s}}}]}"
                + ", projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}}";

        TargetDataAccessor targetDataAccessor =
            targetDataAccessorFactory.create("mongo_reader", template, "orderNo", null);

        TargetRows targetRows;
        GroupRows groupRows;

        targetRows = targetDataAccessor.queryTargetRows(Arrays.asList("1", "2", "3"));
        Assertions.assertEquals(3, targetRows.size());

        groupRows = targetRows.removeRelateGroup("1");
        Assertions.assertEquals(2, targetRows.getRemainGroups().size());
        Assertions.assertEquals(2, groupRows.getRows().size());
        Assertions.assertEquals("S", groupRows.getRows().get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.0001"), groupRows.getRows().get(0).getItem("amount"));
        Assertions.assertEquals("SS", groupRows.getRows().get(1).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.0002"), groupRows.getRows().get(1).getItem("amount"));

        groupRows = targetRows.removeRelateGroup("2");
        Assertions.assertEquals(1, targetRows.getRemainGroups().size());
        Assertions.assertEquals(1, groupRows.getRows().size());
        Assertions.assertEquals("Fail", groupRows.getRows().get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("123456789012345.1234"), groupRows.getRows().get(0).getItem("amount"));

        groupRows = targetRows.removeRelateGroup("3");
        Assertions.assertEquals(0, targetRows.getRemainGroups().size());
        Assertions.assertEquals(1, groupRows.getRows().size());
        Assertions.assertEquals("Failure", groupRows.getRows().get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.01"), groupRows.getRows().get(0).getItem("amount"));

        targetRows = targetDataAccessor.queryTargetRows(new ArrayList<>());
        Assertions.assertEquals(0, targetRows.size());

        targetRows = targetDataAccessor.queryTargetRows(null);
        Assertions.assertEquals(0, targetRows.size());
    }

    @Test
    public void testAggregate() {
        testDataMocker.reset("unittest_success");
        testDataMocker.targetDataBuilder("1", "S", "0.0001");
        testDataMocker.targetDataBuilder("1", "S", "0.0002");
        testDataMocker.targetDataBuilder("2", "F", "0.001");
        testDataMocker.targetDataBuilder("3", "F", "0.01");
        testDataMocker.mockMongo();

        String template = "{db: 'testdb', aggregate: 'csc_test_data', pipeline: ["
            + "{$match: {$and: [{dataSet: 'unittest_success'}, {status: {$in: {id_s}}}]}}"
            + "{$group: {_id: '$status', totalAmount: {$sum: '$amount'}}}" + "]}";

        TargetDataAccessor targetDataAccessor =
            targetDataAccessorFactory.create("mongo_reader", template, "_id", null);

        TargetRows targetRows;
        GroupRows groupRows;

        targetRows = targetDataAccessor.queryTargetRows(Arrays.asList("S", "F"));
        Assertions.assertEquals(2, targetRows.size());

        groupRows = targetRows.removeRelateGroup("S");
        Assertions.assertEquals(1, targetRows.getRemainGroups().size());
        Assertions.assertEquals(1, groupRows.getRows().size());
        Assertions.assertEquals("S", groupRows.getRows().get(0).getItem("_id"));
        Assertions.assertEquals(new BigDecimal("0.0003"), groupRows.getRows().get(0).getItem("totalAmount"));

        groupRows = targetRows.removeRelateGroup("F");
        Assertions.assertEquals(0, targetRows.getRemainGroups().size());
        Assertions.assertEquals(1, groupRows.getRows().size());
        Assertions.assertEquals("F", groupRows.getRows().get(0).getItem("_id"));
        Assertions.assertEquals(new BigDecimal("0.011"), groupRows.getRows().get(0).getItem("totalAmount"));

        targetRows = targetDataAccessor.queryTargetRows(new ArrayList<>());
        Assertions.assertEquals(0, targetRows.size());

        targetRows = targetDataAccessor.queryTargetRows(null);
        Assertions.assertEquals(0, targetRows.size());
    }

}
