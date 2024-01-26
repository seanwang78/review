package com.uaepay.pub.csc.domainservice.monitor.task.action;

import java.util.ArrayList;
import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;

/**
 * @author zc
 */
public interface MonitorAction {

    /**
     * 查询执行结束，结束计时前
     *
     * @param define
     *            定义
     * @param task
     *            任务
     * @param monitorResult
     *            监控执行结果
     */
    void afterQuery(MonitorDefine define, MonitorTask task, MonitorResult monitorResult);

    /**
     * 更新任务
     * 
     * @param define
     *            定义
     * @param task
     *            任务
     * @param monitorResult
     *            监控执行结果
     */
    void updateTask(MonitorDefine define, MonitorTask task, MonitorResult monitorResult);

    /**
     * 构造任务明细
     * 
     * @param define
     * @param task
     * @param monitorResult
     * @return
     */
    static List<MonitorTaskDetail> buildDetails(MonitorDefine define, MonitorTask task, MonitorResult monitorResult) {
        if (!define.isRecordDetail()) {
            return null;
        }
        List<MonitorTaskDetail> details = new ArrayList<>(monitorResult.size());
        for (RowData row : monitorResult.getDetails()) {
            MonitorTaskDetail detail = new MonitorTaskDetail();
            detail.setTaskId(task.getTaskId());
            detail.setKeyValue(row.getStringRelateValue());
            detail.setDetailContent(row.toString());
            details.add(detail);
        }
        return details;
    }

}
