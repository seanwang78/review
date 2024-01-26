package com.uaepay.pub.csc.domainservice.notify;

import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;

import java.util.Map;

/**
 * 通知模版仓储
 * 
 * @author zc
 */
public interface NotifyTemplateRepository {

    String loadTemplate(String filePath);

    String buildTeamsNotifyInfo(NotifyTemplateTypeEnum templateType, Map<String, Object> params);

}
