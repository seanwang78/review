package com.uaepay.pub.csc.domainservice.monitor.task.action;

import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;

/**
 * 监控动作工厂
 * 
 * @author zc
 */
public interface MonitorActionFactory {

    MonitorAction create(MonitorTypeEnum monitorType);

}
