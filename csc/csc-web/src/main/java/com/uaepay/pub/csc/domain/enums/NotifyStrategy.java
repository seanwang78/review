package com.uaepay.pub.csc.domain.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.domain.notify.NotifyTarget;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;

/**
 * 通知策略
 * 
 * @author zc
 */
public enum NotifyStrategy {

    /**
     * 按告警级别
     */
    ALARM_LEVEL((contact, alarmLevel) -> {
        NotifyTypeEnum notifyType = null;
        if (alarmLevel == AlarmLevelEnum.URGENT) {
            notifyType = contact.getUrgentNotifyType();
        }
        if (notifyType == null) {
            notifyType = contact.getNormalNotifyType();
        }
        if (notifyType == null) {
            notifyType = NotifyTypeEnum.EMAIL;
        }
        List<NotifyTarget> result = new ArrayList<>();
        result.add(new NotifyTarget(NotifyTypeEnum.EMAIL, contact.getEmail()));
        if (notifyType != NotifyTypeEnum.EMAIL) {
            String identity = contact.getNotifyIdentity(notifyType);
            if (StringUtils.isNotBlank(identity)) {
                result.add(new NotifyTarget(notifyType, identity));
            }
        }
        return result;
    }),

    /**
     * 只通知email
     */
    ONLY_EMAIL((contact, alarmLevel) -> {
        if (StringUtils.isBlank(contact.getEmail())) {
            return null;
        }
        return Collections.singletonList(new NotifyTarget(NotifyTypeEnum.EMAIL, contact.getEmail()));
    }),

    ;

    NotifyStrategy(TargetCollector collector) {
        this.collector = collector;
    }

    private final TargetCollector collector;

    public List<NotifyTarget> collect(NotifyContact contact, AlarmLevelEnum alarmLevel) {
        return collector.collect(contact, alarmLevel);
    }

    interface TargetCollector {
        List<NotifyTarget> collect(NotifyContact contact, AlarmLevelEnum alarmLevel);
    }

}
