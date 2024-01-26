package com.uaepay.pub.csc.domainservice.compare.task;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;

/**
 * 对账任务工厂
 * 
 * @author zc
 */
public interface CompareTaskRunnerFactory {

    /**
     * 创建对账任务运行实例
     * 
     * @param task
     *            对账任务
     * @param callback
     *            对账回调
     * @return
     */
    Runnable create(CompareTask task, CompareTaskCallback callback);

}
