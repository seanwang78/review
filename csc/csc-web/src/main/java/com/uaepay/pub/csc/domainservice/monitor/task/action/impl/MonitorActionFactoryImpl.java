package com.uaepay.pub.csc.domainservice.monitor.task.action.impl;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorAction;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorActionFactory;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;

/**
 * @author zc
 */
@Service
public class MonitorActionFactoryImpl implements MonitorActionFactory {

    public MonitorActionFactoryImpl(ReportMonitorFactory reportMonitorFactory, AlarmActionFactory alarmActionFactory) {
        this.reportMonitorFactory = reportMonitorFactory;
        this.alarmActionFactory = alarmActionFactory;
    }

    ReportMonitorFactory reportMonitorFactory;

    AlarmActionFactory alarmActionFactory;

    @Override
    public MonitorAction create(MonitorTypeEnum monitorType) {
        if (monitorType == MonitorTypeEnum.REPORT) {
            return reportMonitorFactory.create();
        } else if (monitorType == MonitorTypeEnum.ALARM) {
            return alarmActionFactory.create();
        }
        throw new ErrorException("监控类型不支持");
    }

}
