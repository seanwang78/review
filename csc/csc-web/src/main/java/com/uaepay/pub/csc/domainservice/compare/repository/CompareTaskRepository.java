package com.uaepay.pub.csc.domainservice.compare.repository;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;

/**
 * @author zc
 */
public interface CompareTaskRepository {

    void saveTask(CompareTask compareTask);

    void updateSuccess(CompareTask compareTask);

    void updateError(CompareTask compareTask);

    void updateFail(CompareTask compareTask, List<CompareDetail> compareDetails, boolean compensate);

    void updateLastRetryTask(CompareTask origTaskId, long retryTaskId);

    void updateRetrySuccess(CompareTask origTask);

    void updateCompensateFinished(CompareTask task);

    void updateStatus(CompareTask task, TaskStatusEnum newStatus);

    /**
     * 更新为已人工确认
     *
     * @param task
     *            任务
     * @param errorMessage
     *            错误信息
     */
    void updateManualConfirmed(CompareTask task, String errorMessage);

    /**
     * 更新为已人工确认
     *
     * @param taskIds
     *            任务列表
     * @param errorMessage
     *            错误信息
     * @return 更新成功行数
     */
    int updateManualConfirmedBatch(List<Long> taskIds, String errorMessage);

    /**
     * 根据任务号获取任务
     * 
     * @param taskId
     *            任务号
     * @return 对账任务，不存在返回null
     */
    CompareTask getTask(long taskId);

    /**
     * 查询等待补单的明细
     * 
     * @param taskId
     *            任务id
     * @return 明细列表
     */
    List<CompareDetail> listWaitCompensateDetails(long taskId);

    /**
     * 更新明细补单状态
     */
    void updateDetailCompensate(CompareDetail detail, CompensateFlagEnum newStatus);

}
