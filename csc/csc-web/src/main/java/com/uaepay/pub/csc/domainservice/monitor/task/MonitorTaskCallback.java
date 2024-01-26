package com.uaepay.pub.csc.domainservice.monitor.task;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;

/**
 * 监控任务回调
 * 
 * @author zc
 */
public interface MonitorTaskCallback {

    // /**
    // * 是否执行补单相关流程
    // * @return 返回true则执行补单流程，否则不执行
    // */
    // boolean enableCompensate();

    /**
     * 保存任务同一个事务执行（在保存完任务之后）
     *
     * @param task
     *            任务
     */
    void onSaveTask(MonitorTask task);

    /**
     * 异常回调
     * 
     * @param e
     *            异常
     */
    void onError(Throwable e);

    /**
     * 完成回调
     * 
     * @param task
     *            任务
     * @param result
     *            对账结果
     */
    void onResult(MonitorTask task, MonitorResult result);

}
