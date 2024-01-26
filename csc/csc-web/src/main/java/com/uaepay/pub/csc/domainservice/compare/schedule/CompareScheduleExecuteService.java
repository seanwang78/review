package com.uaepay.pub.csc.domainservice.compare.schedule;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;

/**
 * 计划执行服务
 * 
 * @author zc
 */
public interface CompareScheduleExecuteService {

    /**
     * 执行计划
     * 
     * @param compareSchedule
     */
    void execute(CompareSchedule compareSchedule) throws TaskFullException;

}
