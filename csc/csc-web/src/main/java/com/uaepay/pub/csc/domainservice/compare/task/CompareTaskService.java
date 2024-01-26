package com.uaepay.pub.csc.domainservice.compare.task;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;

/**
 * 对账任务服务
 * 
 * @author zc
 */
public interface CompareTaskService {

    /**
     * 申请后台任务，使用CallRunPolicy
     * 
     * @param task
     *            任务
     * @param callback
     *            回调
     */
    void applyDaemonTask(CompareTask task, CompareTaskCallback callback);

    /**
     * 申请任务
     *
     * @param compareTask
     *            对账任务
     * @param callback
     *            回调，可空
     * @return 任务号
     * @throws TaskFullException
     *             任务已满
     */
    void applyTask(CompareTask compareTask, CompareTaskCallback callback) throws TaskFullException;

}
