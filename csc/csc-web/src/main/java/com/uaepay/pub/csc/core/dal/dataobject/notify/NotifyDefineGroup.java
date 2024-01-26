package com.uaepay.pub.csc.core.dal.dataobject.notify;

import java.util.Date;

import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;

public class NotifyDefineGroup {
    /**
     * 关联ID
     */
    private Long relateId;

    /**
     * 定义类型：C=对账，M=监控
     */
    private DefineTypeEnum defineType;

    /**
     * 定义ID
     */
    private Long defineId;

    /**
     * 组ID
     */
    private Long groupId;

    /**
     * 创建时间
     */
    private Date createTime;

    public Long getRelateId() {
        return relateId;
    }

    public void setRelateId(Long relateId) {
        this.relateId = relateId;
    }

    public DefineTypeEnum getDefineType() {
        return defineType;
    }

    public void setDefineType(DefineTypeEnum defineType) {
        this.defineType = defineType;
    }

    public Long getDefineId() {
        return defineId;
    }

    public void setDefineId(Long defineId) {
        this.defineId = defineId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}