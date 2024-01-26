package com.uaepay.pub.csc.test.mocker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.test.builder.TestDataBuilder;
import com.uaepay.pub.csc.test.dal.CompareTestMapper;
import com.uaepay.pub.csc.test.dal.es.TestDataEsMapper;
import com.uaepay.pub.csc.test.dal.mongo.TestDataMongoMapper;
import com.uaepay.pub.csc.test.domain.TestData;

@NotThreadSafe
@Service
public class TestDataMocker {

    @Autowired
    private CompareTestMapper compareTestMapper;

    @Autowired
    private TestDataMongoMapper testDataMongoMapper;

    @Autowired
    private TestDataEsMapper testDataEsMapper;

    private String dataSet;
    private List<TestDataBuilder> builders = new ArrayList<>();

    public TestDataMocker reset(String dataSet) {
        this.dataSet = dataSet;
        this.builders.clear();
        return this;
    }

    public TestDataBuilder srcDataBuilder(String orderNo, String status, String amount) {
        TestDataBuilder builder = TestDataBuilder.newSrcData(dataSet, orderNo, status, amount);
        builders.add(builder);
        return builder;
    }

    public TestDataBuilder targetDataBuilder(String orderNo, String status, String amount) {
        TestDataBuilder builder = TestDataBuilder.newTargetData(dataSet, orderNo, status, amount);
        builders.add(builder);
        return builder;
    }

    public TestDataBuilder monitorDataBuilder(String orderNo, String status, String amount) {
        return monitorDataBuilder("M", orderNo, status, amount);
    }

    public TestDataBuilder monitorDataBuilder(String dataType, String orderNo, String status, String amount) {
        TestDataBuilder builder = TestDataBuilder.newMonitorData(dataSet, orderNo, status, amount).dataType(dataType);
        builders.add(builder);
        return builder;
    }

    public TestDataBuilder monitorDataBuilder(String group1, String group2, String group3, String orderNo,
        String status, String amount) {
        TestDataBuilder builder =
            TestDataBuilder.newMonitorData(group1, group2, group3, dataSet, orderNo, status, amount);
        builders.add(builder);
        return builder;
    }

    public void mock() {
        int delete = compareTestMapper.deleteTestDataByDataSet(dataSet);
        if (delete != 0) {
            System.out.printf("删除测试数据: %s, count=%d\n", dataSet, delete);
        }
        for (TestDataBuilder builder : builders) {
            TestData testData = builder.build();
            System.out.printf("mock数据: %s\n", testData);
            compareTestMapper.insertTestData(testData);
        }
    }

    public void mockMongo() {
        long delete = testDataMongoMapper.deleteByDataSet(dataSet);
        if (delete != 0) {
            System.out.printf("删除mongo测试数据: %s, count=%d\n", dataSet, delete);
        }
        for (TestDataBuilder builder : builders) {
            TestData testData = builder.build();
            System.out.printf("mock数据: %s\n", testData);
            testDataMongoMapper.insertTestData(testData);
        }
    }

    public void mockEs() {
        Set<String> shardingList = new HashSet<>();
        shardingList.add(null);
        for (TestDataBuilder builder : builders) {
            String sharding = builder.getSharding();
            if (StringUtils.isNotBlank(sharding)) {
                shardingList.add(sharding);
            }
        }
        // 删除数据集
        for (String sharding : shardingList) {
            long delete = testDataEsMapper.deleteByDataSet(dataSet, sharding);
            if (delete != 0) {
                System.out.printf("删除es测试数据: %s, %s, count=%d\n", dataSet, sharding, delete);
            }
        }
        // 保存数据集
        for (TestDataBuilder builder : builders) {
            TestData testData = builder.build();
            String sharding = builder.getSharding();
            System.out.printf("mock数据: %s, %s\n", testData, sharding);
            testDataEsMapper.insertTestData(testData, sharding);
        }
        try {
            // 等索引延迟
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
