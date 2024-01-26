package com.uaepay.pub.csc.cases.domainservice.compare.data.es;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.domain.compare.GroupRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessorFactory;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class EsTargetDataAccessorTest extends MockTestBase {

    @Autowired
    private TargetDataAccessorFactory targetDataAccessorFactory;

    @Autowired
    private TestDataMocker testDataMocker;

    @Test
    public void test_query_page() throws InterruptedException {
        testDataMocker.reset("unittest_success");
        testDataMocker.targetDataBuilder("1", "S", "0.0001");
        testDataMocker.targetDataBuilder("1", "SS", "0.0002");
        testDataMocker.targetDataBuilder("2", "Fail", "123456789012345.6789");
        testDataMocker.targetDataBuilder("3", "Failure", "0.01");
        testDataMocker.mockEs();

        String template = "{\"index\": \"i_dev_csc_test_data\""
            + ", \"query\": {\"bool\": {\"filter\": [{\"match\": {\"dataSet\": \"unittest_success\"}}"
            + ", {\"match\": {\"dataType\": \"T\"}}, {\"terms\": {\"orderNo\": {id_s}}} ]}}"
            + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"] }";

        TargetDataAccessor targetDataAccessor =
            targetDataAccessorFactory.create("es_business_reader", template, "orderNo", null);

        TargetRows targetRows;
        GroupRows groupRows;

        targetRows = targetDataAccessor.queryTargetRows(Arrays.asList("1", "2", "3"));
        Assertions.assertEquals(3, targetRows.size());

        groupRows = targetRows.removeRelateGroup("1");
        Assertions.assertEquals(2, targetRows.getRemainGroups().size());
        Assertions.assertEquals(2, groupRows.getRows().size());
        Assertions.assertEquals("S", groupRows.getRows().get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.00010"), groupRows.getRows().get(0).getItem("amount"));
        Assertions.assertEquals("SS", groupRows.getRows().get(1).getItem("status"));
        Assertions.assertEquals(new BigDecimal("0.00020"), groupRows.getRows().get(1).getItem("amount"));

        groupRows = targetRows.removeRelateGroup("2");
        Assertions.assertEquals(1, targetRows.getRemainGroups().size());
        Assertions.assertEquals(1, groupRows.getRows().size());
        Assertions.assertEquals("Fail", groupRows.getRows().get(0).getItem("status"));
        Assertions.assertEquals(new BigDecimal("123456789012345.67"), groupRows.getRows().get(0).getItem("amount"));

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

}
