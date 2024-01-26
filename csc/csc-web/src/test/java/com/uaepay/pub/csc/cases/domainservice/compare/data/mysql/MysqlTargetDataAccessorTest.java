package com.uaepay.pub.csc.cases.domainservice.compare.data.mysql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.domain.compare.GroupRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessorFactory;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

public class MysqlTargetDataAccessorTest extends MockTestBase {

    @Autowired
    private TargetDataAccessorFactory targetDataAccessorFactory;

    @Autowired
    private TestDataMocker testDataMocker;

    @Test
    public void test() {
        testDataMocker.reset("unittest_success");
        testDataMocker.targetDataBuilder("1", "S", "0.0001");
        testDataMocker.targetDataBuilder("1", "SS", "0.0002");
        testDataMocker.targetDataBuilder("2", "Fail", "123456789012345.1234");
        testDataMocker.targetDataBuilder("3", "Failure", "0.01");
        testDataMocker.mock();

        String template = "select order_no, status, amount, update_time"
            + " from csc.t_test_data where data_set = 'unittest_success' and data_type = 'T'"
            + " and order_no in {id_s}";
        TargetDataAccessor targetDataAccessor = targetDataAccessorFactory.create("default", template, "order_no", null);

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
        Assertions.assertEquals(new BigDecimal("0.0100"), groupRows.getRows().get(0).getItem("amount"));

        targetRows = targetDataAccessor.queryTargetRows(new ArrayList<>());
        Assertions.assertEquals(0, targetRows.size());

        targetRows = targetDataAccessor.queryTargetRows(null);
        Assertions.assertEquals(0, targetRows.size());
    }

}
