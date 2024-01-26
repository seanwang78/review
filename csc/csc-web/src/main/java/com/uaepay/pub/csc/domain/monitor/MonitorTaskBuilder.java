package com.uaepay.pub.csc.domain.monitor;

import java.util.Date;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskTypeEnum;

import lombok.Data;

/**
 * 监控任务请求
 * 
 * @author zc
 */
@Data
public class MonitorTaskBuilder {

    private static final String OPERATOR_SYSTEM = "system";

    public MonitorTaskBuilder() {
        result = new MonitorTask();
        result.setTaskStatus(MonitorTaskStatusEnum.PROCESS);
    }

    private MonitorTask result;

    public MonitorTaskBuilder manual(long defineId, String operator) {
        result.setTaskType(TaskTypeEnum.MANUAL);
        result.setDefineId(defineId);
        result.setOperator(operator);
        return this;
    }

    public MonitorTaskBuilder schedule(long defineId, long scheduleId) {
        result.setTaskType(TaskTypeEnum.SCHEDULE);
        result.setDefineId(defineId);
        result.setOperator(OPERATOR_SYSTEM);
        result.setMemo("schedule-" + scheduleId);
        return this;
    }

    public MonitorTaskBuilder retryTask(long defineId, long origTaskId, String operator) {
        result.setTaskType(TaskTypeEnum.RETRY);
        result.setDefineId(defineId);
        result.setOrigTaskId(origTaskId);
        result.setOperator(operator);
        return this;
    }

    public MonitorTaskBuilder dataTime(Date dataBeginTime, Date dataEndTime) {
        result.setDataBeginTime(dataBeginTime);
        result.setDataEndTime(dataEndTime);
        return this;
    }

    public MonitorTask build() {
        return result;
    }

}
