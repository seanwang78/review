package com.uaepay.pub.csc.core.dal.dataobject.notify;

import java.util.Date;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NotifyContact {
    /**
     * 联系人ID
     */
    private Long contactId;

    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * TotokID
     */
    private String totokId;

    /**
     * 普通通知类型：EMAIL,SMS,TOTOK
     */
    private NotifyTypeEnum normalNotifyType;

    /**
     * 紧急通知类型：EMAIL,SMS,TOTOK
     */
    private NotifyTypeEnum urgentNotifyType;

    /**
     * 启用标识：Y=启用，N=停用
     */
    private YesNoEnum enableFlag;

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

    public String getNotifyIdentity(NotifyTypeEnum notifyType) {
        switch (notifyType) {
            case EMAIL:
                return email;
            case SMS:
                return mobile;
            default:
                return null;
        }
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTotokId() {
        return totokId;
    }

    public void setTotokId(String totokId) {
        this.totokId = totokId;
    }

    public NotifyTypeEnum getNormalNotifyType() {
        return normalNotifyType;
    }

    public void setNormalNotifyType(NotifyTypeEnum normalNotifyType) {
        this.normalNotifyType = normalNotifyType;
    }

    public NotifyTypeEnum getUrgentNotifyType() {
        return urgentNotifyType;
    }

    public void setUrgentNotifyType(NotifyTypeEnum urgentNotifyType) {
        this.urgentNotifyType = urgentNotifyType;
    }

    public YesNoEnum getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(YesNoEnum enableFlag) {
        this.enableFlag = enableFlag;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}