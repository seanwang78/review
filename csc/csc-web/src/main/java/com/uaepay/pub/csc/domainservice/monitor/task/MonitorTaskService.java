package com.uaepay.pub.csc.domainservice.monitor.task;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;

/**
 * 监控任务服务
 * 
 * @author zc
 */
public interface MonitorTaskService {

    /**
     * 申请后台任务
     * 
     * @param task
     *            任务
     * @param callback
     *            回调
     */
    void applyDaemonTask(MonitorTask task, MonitorTaskCallback callback);

    /**
     * 申请任务
     *
     * @param task
     *            任务
     * @param callback
     *            回调
     * @return 任务号
     * @throws TaskFullException
     *             任务已满
     */
    void applyTask(MonitorTask task, MonitorTaskCallback callback) throws TaskFullException;

}
