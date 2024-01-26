package com.uaepay.pub.csc.manual;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.mocker.TestDataMocker;

@Disabled
public class TestDataMockerTest extends MockTestBase {

    @Autowired
    TestDataMocker testDataMocker;

    @Test
    public void test() {
        testDataMocker.reset("TestDataMockerTest");
        testDataMocker.srcDataBuilder("a", "S", "0.0001").updateTime("2020-12-01 05:00:00");
        testDataMocker.srcDataBuilder("b", "F", "123456789012345.6789").updateTime("2020-12-02 06:30:00");
        testDataMocker.srcDataBuilder("c", "F", "0.01").updateTime("2020-12-02 07:00:00");
        testDataMocker.srcDataBuilder("d", "F", "0.02").updateTime("2020-12-01 08:00:00");
        testDataMocker.targetDataBuilder("a", "Success", "0.0001").sharding("20201201");
        testDataMocker.targetDataBuilder("b", "Success", "123456789012345.6789").sharding("20201202");
        testDataMocker.targetDataBuilder("c", "Failure", "0.02").sharding("20201202");
        testDataMocker.mockEs();
    }

}
