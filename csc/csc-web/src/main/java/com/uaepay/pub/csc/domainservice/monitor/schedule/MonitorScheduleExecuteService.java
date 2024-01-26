package com.uaepay.pub.csc.domainservice.monitor.schedule;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;

/**
 * 计划执行服务
 * 
 * @author zc
 */
public interface MonitorScheduleExecuteService {

    /**
     * 执行计划
     * 
     * @param schedule 计划
     */
    void execute(MonitorSchedule schedule) throws TaskFullException;

}
