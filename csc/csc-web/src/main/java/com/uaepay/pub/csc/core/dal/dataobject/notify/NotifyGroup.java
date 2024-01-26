package com.uaepay.pub.csc.core.dal.dataobject.notify;

import java.util.Date;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;

public class NotifyGroup {
    /**
     * 组ID
     */
    private Long groupId;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 是否主通知：Y=是，N=否
     */
    private YesNoEnum isMain;

    /**
     * Teams频道url
     */
    private String teamsUrl;

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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public YesNoEnum getIsMain() {
        return isMain;
    }

    public void setIsMain(YesNoEnum isMain) {
        this.isMain = isMain;
    }

    public String getTeamsUrl() {
        return teamsUrl;
    }

    public void setTeamsUrl(String teamsUrl) {
        this.teamsUrl = teamsUrl;
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
}