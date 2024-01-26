package com.uaepay.pub.csc.core.dal.dataobject.notify;

import java.util.Date;

public class NotifyGroupContact {
    /**
     * 关联ID
     */
    private Long relateId;

    /**
     * 组ID
     */
    private Long groupId;

    /**
     * 联系人ID
     */
    private Long contactId;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}