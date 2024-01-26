package com.uaepay.pub.csc.domainservice.monitor.task;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;

/**
 * 监控任务工厂
 * 
 * @author zc
 */
public interface MonitorTaskRunnerFactory {

    /**
     * 创建任务运行实例
     * 
     * @param define
     *            定义
     * @param task
     *            任务
     * @param callback
     *            回调
     * @return
     */
    Runnable create(MonitorDefine define, MonitorTask task, MonitorTaskCallback callback);

}
