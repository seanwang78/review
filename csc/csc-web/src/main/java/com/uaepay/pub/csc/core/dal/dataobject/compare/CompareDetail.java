package com.uaepay.pub.csc.core.dal.dataobject.compare;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.service.facade.enums.CompareStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;

public class CompareDetail {

    public static final String NOTIFY_STATUS = "notifyStatus";

    /**
     * 明细ID
     */
    private Long detailId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 关联标志
     */
    private String relateIdentity;

    /**
     * 源数据
     */
    private String srcData;

    /**
     * 目标数据
     */
    private String targetData;

    /**
     * 比对状态：L=少数据，M=不一致
     */
    private CompareStatusEnum compareStatus;

    /**
     * 补单标志
     */
    private CompensateFlagEnum compensateFlag;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 扩展参数
     */
    private Map<String, String> extension;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getExtension(String key) {
        if (extension == null) {
            return null;
        } else {
            return extension.get(key);
        }
    }

    public void putExtension(String key, String value) {
        if (extension == null) {
            extension = new HashMap<>();
        }
        extension.put(key, value);
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getRelateIdentity() {
        return relateIdentity;
    }

    public void setRelateIdentity(String relateIdentity) {
        this.relateIdentity = relateIdentity;
    }

    public String getSrcData() {
        return srcData;
    }

    public void setSrcData(String srcData) {
        this.srcData = srcData;
    }

    public String getTargetData() {
        return targetData;
    }

    public void setTargetData(String targetData) {
        this.targetData = targetData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public CompareStatusEnum getCompareStatus() {
        return compareStatus;
    }

    public void setCompareStatus(CompareStatusEnum compareStatus) {
        this.compareStatus = compareStatus;
    }

    public CompensateFlagEnum getCompensateFlag() {
        return compensateFlag;
    }

    public void setCompensateFlag(CompensateFlagEnum compensateFlag) {
        this.compensateFlag = compensateFlag;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }

}