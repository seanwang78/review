package com.uaepay.pub.csc.core.dal.dataobject.compare;

import java.util.Date;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;
import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskTypeEnum;

public class CompareTask {
    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 定义ID
     */
    private Long defineId;

    /**
     * 任务类型：M=人工，S=计划，R=重试
     */
    private TaskTypeEnum taskType;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 原任务ID
     */
    private Long origTaskId;

    /**
     * 数据开始时间
     */
    private Date dataBeginTime;

    /**
     * 数据结束时间
     */
    private Date dataEndTime;

    /**
     * 任务状态：P=处理中，S=一致，F=不一致，CP=补单中，RW=等待重试，RS=重试成功
     */
    private TaskStatusEnum taskStatus;

    /**
     * 对账统计
     */
    private String compareStatistic;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 最后重试任务ID
     */
    private Long lastRetryTaskId;

    /**
     * 对账耗时：单位：秒
     */
    private Integer compareConsume;

    /**
     * 扩展参数
     */
    private Map<String, String> extension;

    /**
     * 备注
     */
    private String memo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    public void setError(CodeMessageEnum codeMessage) {
        setError(codeMessage, codeMessage.getMessage());
    }

    public void setError(CodeEnum code, String message) {
        this.errorCode = code.getCode();
        this.errorMessage = message;
    }

    public void setError(String code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getDefineId() {
        return defineId;
    }

    public void setDefineId(Long defineId) {
        this.defineId = defineId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getOrigTaskId() {
        return origTaskId;
    }

    public void setOrigTaskId(Long origTaskId) {
        this.origTaskId = origTaskId;
    }

    public Date getDataBeginTime() {
        return dataBeginTime;
    }

    public void setDataBeginTime(Date dataBeginTime) {
        this.dataBeginTime = dataBeginTime;
    }

    public Date getDataEndTime() {
        return dataEndTime;
    }

    public void setDataEndTime(Date dataEndTime) {
        this.dataEndTime = dataEndTime;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getLastRetryTaskId() {
        return lastRetryTaskId;
    }

    public void setLastRetryTaskId(Long lastRetryTaskId) {
        this.lastRetryTaskId = lastRetryTaskId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public TaskTypeEnum getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    public TaskStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

    public Integer getCompareConsume() {
        return compareConsume;
    }

    public void setCompareConsume(Integer compareConsume) {
        this.compareConsume = compareConsume;
    }

    public String getCompareStatistic() {
        return compareStatistic;
    }

    public void setCompareStatistic(String compareStatistic) {
        this.compareStatistic = compareStatistic;
    }
}