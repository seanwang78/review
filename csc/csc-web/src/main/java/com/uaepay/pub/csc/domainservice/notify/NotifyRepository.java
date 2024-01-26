package com.uaepay.pub.csc.domainservice.notify;

import java.util.List;
import java.util.Map;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;

/**
 * 通知仓储
 * 
 * @author zc
 */
public interface NotifyRepository {

    /**
     * 加载通知联系人分组
     * 
     * @param notifyTemplateType 通知模版类型
     *            定义类型
     * @param defineId
     *            定义id
     * @return 通知联系人按通知方式分组
     */
    Map<NotifyTypeEnum, List<String>> loadNotifyGroup(NotifyTemplateTypeEnum notifyTemplateType, long defineId,
        AlarmLevelEnum alarmLevel);


    /**
     * 获取teams频道通知url列表
     *
     * * @param notifyTemplateType 通知模版类型
     *      *            定义类型
     * @param defineId
     *            定义id
     * @return Map<groupName, url>
     */
    Map<String, String> getTeamsUrlList(NotifyTemplateTypeEnum notifyTemplateType, long defineId);

}
