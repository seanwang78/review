package com.uaepay.pub.csc.domain.compare;

import java.util.Date;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskTypeEnum;

import lombok.Data;

/**
 * 对账任务请求
 * 
 * @author zc
 */
@Data
public class CompareTaskBuilder {

    private static final String OPERATOR_SYSTEM = "system";

    public CompareTaskBuilder() {
        result = new CompareTask();
        result.setTaskStatus(TaskStatusEnum.PROCESS);
    }

    private CompareTask result;

    public CompareTaskBuilder manual(long defineId, String operator) {
        result.setTaskType(TaskTypeEnum.MANUAL);
        result.setDefineId(defineId);
        result.setOperator(operator);
        return this;
    }

    public CompareTaskBuilder schedule(long scheduleId, long defineId) {
        result.setTaskType(TaskTypeEnum.SCHEDULE);
        result.setDefineId(defineId);
        result.setOperator(OPERATOR_SYSTEM);
        result.setMemo("schedule-" + scheduleId);
        return this;
    }

    public CompareTaskBuilder retryTask(long defineId, long origTaskId, String operator) {
        result.setTaskType(TaskTypeEnum.RETRY);
        result.setDefineId(defineId);
        result.setOrigTaskId(origTaskId);
        result.setOperator(operator);
        return this;
    }

    public CompareTaskBuilder dataTime(Date dataBeginTime, Date dataEndTime) {
        result.setDataBeginTime(dataBeginTime);
        result.setDataEndTime(dataEndTime);
        return this;
    }

    public CompareTask build() {
        return result;
    }

}
