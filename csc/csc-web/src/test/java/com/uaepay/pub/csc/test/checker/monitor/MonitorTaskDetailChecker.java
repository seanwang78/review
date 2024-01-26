package com.uaepay.pub.csc.test.checker.monitor;

import org.junit.jupiter.api.Assertions;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;

public class MonitorTaskDetailChecker {

    public MonitorTaskDetailChecker(MonitorTaskDetail detail) {
        this.detail = detail;
    }

    private MonitorTaskDetail detail;

    public MonitorTaskDetailChecker identity(String keyValue) {
        Assertions.assertEquals(keyValue, detail.getKeyValue());
        return this;
    }

    public MonitorTaskDetailChecker content(String detailContent) {
        Assertions.assertEquals(detailContent, detail.getDetailContent());
        return this;
    }

}
