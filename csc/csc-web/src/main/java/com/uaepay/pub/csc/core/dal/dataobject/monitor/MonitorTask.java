package com.uaepay.pub.csc.core.dal.dataobject.monitor;

import java.util.Date;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;
import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.MonitorTaskStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskTypeEnum;

public class MonitorTask {

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 定义ID
     */
    private Long defineId;

    /**
     * 监控类型：R=报表，A=报警
     */
    private MonitorTypeEnum monitorType;

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
     * 任务状态：P=处理中，S=无报警或报表，F=报警，RS=重试无报警，E=异常
     */
    private MonitorTaskStatusEnum taskStatus;

    /**
     * 明细数量
     */
    private Integer detailCount;

    /**
     * 是否全部明细：Y=是，N=否
     */
    private YesNoEnum isAllDetail;

    /**
     * 报警登记：I=忽略，N=普通，U=紧急
     */
    private AlarmLevelEnum alarmLevel;

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
     * 执行耗时：单位：秒
     */
    private Integer executeConsume;

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

    public Integer getExecuteConsume() {
        return executeConsume;
    }

    public void setExecuteConsume(Integer executeConsume) {
        this.executeConsume = executeConsume;
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

    public MonitorTypeEnum getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(MonitorTypeEnum monitorType) {
        this.monitorType = monitorType;
    }

    public TaskTypeEnum getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskTypeEnum taskType) {
        this.taskType = taskType;
    }

    public MonitorTaskStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(MonitorTaskStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getDetailCount() {
        return detailCount;
    }

    public void setDetailCount(Integer detailCount) {
        this.detailCount = detailCount;
    }

    public YesNoEnum getIsAllDetail() {
        return isAllDetail;
    }

    public void setIsAllDetail(YesNoEnum isAllDetail) {
        this.isAllDetail = isAllDetail;
    }

    public AlarmLevelEnum getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(AlarmLevelEnum alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }
}