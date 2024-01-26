package com.uaepay.pub.csc.domainservice.monitor.task;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;

/**
 * 重试任务服务
 * 
 * @author zc
 */
public interface MonitorRetryTaskService {

    /**
     * 重试任务
     * 
     * @param origTask
     *            原任务
     * @return 重试任务id，如果申请异常报错
     */
    long retry(MonitorTask origTask, String operator);

}
