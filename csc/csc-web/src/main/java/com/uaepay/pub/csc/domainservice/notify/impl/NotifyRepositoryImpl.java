package com.uaepay.pub.csc.domainservice.notify.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.core.dal.mapper.notify.NotifyGroupMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.mapper.notify.NotifyContactMapper;
import com.uaepay.pub.csc.domain.enums.NotifyStrategy;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domain.notify.NotifyTarget;
import com.uaepay.pub.csc.domainservice.notify.NotifyRepository;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;

/**
 * @author zc
 */
@Service
public class NotifyRepositoryImpl implements NotifyRepository {

    @Autowired
    NotifyContactMapper notifyContactMapper;

    @Autowired
    NotifyGroupMapper notifyGroupMapper;

    @Override
    public Map<NotifyTypeEnum, List<String>> loadNotifyGroup(NotifyTemplateTypeEnum notifyTemplateType, long defineId,
                                                             AlarmLevelEnum alarmLevel) {
        List<NotifyContact> contacts =
                notifyContactMapper.selectByDefineId(notifyTemplateType.getDefineType(), defineId);
        if (CollectionUtils.isEmpty(contacts)) {
            return null;
        }
        Map<NotifyTypeEnum, List<String>> result = new HashMap<>(2);
        NotifyStrategy strategy = notifyTemplateType.getNotifyStrategy();
        for (NotifyContact contact : contacts) {
            List<NotifyTarget> targets = strategy.collect(contact, alarmLevel);
            if (CollectionUtils.isNotEmpty(targets)) {
                targets.forEach((target) -> {
                    NotifyTypeEnum notifyType = target.getNotifyType();
                    if (!result.containsKey(notifyType)) {
                        result.put(notifyType, new ArrayList<>());
                    }
                    result.get(notifyType).add(target.getTargetIdentity());
                });
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getTeamsUrlList(NotifyTemplateTypeEnum notifyTemplateType, long defineId) {
        Map<String, String> result = new HashMap<>();
        List<NotifyGroup> groups = notifyGroupMapper.selectGroupByDefineId(notifyTemplateType.getDefineType(), defineId);
        Map<YesNoEnum, Map<String, String>> resultYesNoMap = new HashMap<>();
        for (NotifyGroup notifyGroup : groups) {
            if (StringUtils.isNotBlank(notifyGroup.getTeamsUrl())) {
                //如果isMain为空则默认设置为N
                if (notifyGroup.getIsMain() == null) {
                    notifyGroup.setIsMain(YesNoEnum.NO);
                }
                if (!resultYesNoMap.containsKey(notifyGroup.getIsMain())) {
                    resultYesNoMap.put(notifyGroup.getIsMain(), new HashMap<>());
                }
                resultYesNoMap.get(notifyGroup.getIsMain()).put(notifyGroup.getGroupName(), notifyGroup.getTeamsUrl());
            }
        }
        if (resultYesNoMap.containsKey(YesNoEnum.YES)) {
            result = resultYesNoMap.get(YesNoEnum.YES);
        } else if (resultYesNoMap.containsKey(YesNoEnum.NO)) {
            result = resultYesNoMap.get(YesNoEnum.NO);
        }
        return result;
    }

}
