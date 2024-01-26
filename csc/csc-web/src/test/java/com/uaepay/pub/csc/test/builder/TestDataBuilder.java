package com.uaepay.pub.csc.test.builder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.uaepay.pub.csc.test.domain.TestData;

public class TestDataBuilder {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final FastDateFormat DATE_FORMAT_00 =
        FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("+00"));

    public static TestDataBuilder newSrcData(String dataSet, String orderNo, String status, String amount) {
        TestData testData = new TestData();
        testData.setDataSet(dataSet);
        testData.setDataType("S");
        testData.setOrderNo(orderNo);
        testData.setStatus(status);
        testData.setAmount(new BigDecimal(amount));
        return new TestDataBuilder(testData);
    }

    public static TestDataBuilder newTargetData(String dataSet, String orderNo, String status, String amount) {
        TestData testData = new TestData();
        testData.setDataSet(dataSet);
        testData.setDataType("T");
        testData.setUpdateTime(new Date());
        testData.setOrderNo(orderNo);
        testData.setStatus(status);
        testData.setAmount(new BigDecimal(amount));
        return new TestDataBuilder(testData);
    }

    public static TestDataBuilder newMonitorData(String dataSet, String orderNo, String status, String amount) {
        TestData testData = new TestData();
        testData.setDataSet(dataSet);
        testData.setDataType("M");
        testData.setUpdateTime(new Date());
        testData.setOrderNo(orderNo);
        testData.setStatus(status);
        testData.setAmount(new BigDecimal(amount));
        return new TestDataBuilder(testData);
    }

    public static TestDataBuilder newMonitorData(String group1, String group2, String group3, String dataSet,
        String orderNo, String status, String amount) {
        TestData testData = new TestData();
        testData.setGroup1(group1);
        testData.setGroup2(group2);
        testData.setGroup3(group3);

        testData.setDataSet(dataSet);
        testData.setDataType("M");
        testData.setUpdateTime(new Date());
        testData.setOrderNo(orderNo);
        testData.setStatus(status);
        testData.setAmount(new BigDecimal(amount));
        return new TestDataBuilder(testData);
    }

    private TestDataBuilder(TestData testData) {
        this.testData = testData;
    }

    private TestData testData;

    private String sharding;

    public TestDataBuilder currency(String currency) {
        testData.setCurrency(currency);
        return this;
    }

    public TestDataBuilder group1(String group1) {
        testData.setGroup1(group1);
        return this;
    }

    public TestDataBuilder updateTime(String updateTime) {
        try {
            Date result;
            if (updateTime.contains("T") && updateTime.contains("Z")) {
                result = DATE_FORMAT_00.parse(updateTime);
            } else {
                result = DATE_FORMAT.parse(updateTime);
            }
            testData.setUpdateTime(result);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public static void main(String[] args) throws ParseException {
        Date date = DATE_FORMAT_00.parse("2022-03-01T00:30:00Z");
        System.out.println(new DateTime(date).withZone(DateTimeZone.forID("+00")).toString("yyyy-MM-dd HH:mm:ssZZZ"));
    }

    public TestDataBuilder updateTime(DateTime updateTime) {
        testData.setUpdateTime(updateTime.toDate());
        return this;
    }

    public TestDataBuilder dataType(String dataType) {
        testData.setDataType(dataType);
        return this;
    }

    public TestDataBuilder sharding(String sharding) {
        this.sharding = sharding;
        return this;
    }

    public TestData build() {
        return testData;
    }

    public String getSharding() {
        return sharding;
    }

}
