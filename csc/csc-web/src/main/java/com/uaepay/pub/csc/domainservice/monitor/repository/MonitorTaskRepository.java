package com.uaepay.pub.csc.domainservice.monitor.repository;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;

/**
 * @author zc
 */
public interface MonitorTaskRepository {

    /**
     * 保存任务
     * 
     * @param task
     *            任务
     */
    void saveTask(MonitorTask task);

    /**
     * 根据任务号获取任务
     *
     * @param taskId
     *            任务号
     * @return 任务，不存在返回null
     */
    MonitorTask getTask(long taskId);

    /**
     * 更新任务成功
     * 
     * @param task
     *            任务
     */
    void updateSuccess(MonitorTask task, List<MonitorTaskDetail> details);

    /**
     * 更新任务失败
     *
     * @param task
     *            任务
     * @param details
     *            明细列表
     */
    void updateFail(MonitorTask task, List<MonitorTaskDetail> details);

    /**
     * 更新任务异常
     * 
     * @param task
     *            任务
     */
    void updateError(MonitorTask task);

    /**
     * 更新最后重试的任务号
     * 
     * @param origTask
     *            原任务
     * @param retryTaskId
     *            重试任务号
     */
    void updateLastRetryTask(MonitorTask origTask, long retryTaskId);

    /**
     * 更新重试成功
     * 
     * @param origTask
     *            原任务
     */
    void updateRetrySuccess(MonitorTask origTask);

    /**
     * 更新为已人工确认
     * 
     * @param task
     *            任务
     * @param errorMessage
     *            错误信息
     */
    void updateManualConfirmed(MonitorTask task, String errorMessage);

    /**
     * 更新为已人工确认-批量
     *
     * @param taskIds
     *            任务号列表
     * @param errorMessage
     *            错误信息
     * @return 更新成功行数
     */
    int updateManualConfirmedBatch(List<Long> taskIds, String errorMessage);

}
