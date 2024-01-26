package com.uaepay.pub.csc.core.dal.dataobject.monitor;

import java.util.Date;
import java.util.Map;

import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

public class MonitorSchedule {
    /**
     * 计划ID
     */
    private Long scheduleId;

    /**
     * 定义ID
     */
    private Long defineId;

    /**
     * 对账时长
     */
    private Integer checkMinutes;

    /**
     * 延迟时长
     */
    private Integer delayMinutes;

    /**
     * 当前进度
     */
    private Date checkedTime;

    /**
     * 下次允许触发时间
     */
    private Date nextTriggerTime;

    /**
     * 当前任务
     */
    private Long currentTaskId;

    /**
     * 版本号
     */
    private Long updateVersion;

    /**
     * 异常次数
     */
    private Integer errorCount;

    /**
     * 计划状态：Y=启用，N=停用，E=异常停用
     */
    private ScheduleStatusEnum scheduleStatus;

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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getDefineId() {
        return defineId;
    }

    public void setDefineId(Long defineId) {
        this.defineId = defineId;
    }

    public Integer getCheckMinutes() {
        return checkMinutes;
    }

    public void setCheckMinutes(Integer checkMinutes) {
        this.checkMinutes = checkMinutes;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public Date getCheckedTime() {
        return checkedTime;
    }

    public void setCheckedTime(Date checkedTime) {
        this.checkedTime = checkedTime;
    }

    public Date getNextTriggerTime() {
        return nextTriggerTime;
    }

    public void setNextTriggerTime(Date nextTriggerTime) {
        this.nextTriggerTime = nextTriggerTime;
    }

    public Long getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(Long currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public Long getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(Long updateVersion) {
        this.updateVersion = updateVersion;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public ScheduleStatusEnum getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(ScheduleStatusEnum scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
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
}