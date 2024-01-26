package com.uaepay.pub.csc.domainservice.notify;

import java.util.Map;
import java.util.function.Supplier;

import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domain.notify.MailExtraInfo;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;

/**
 * 通知服务
 * 
 * @author zc
 */
public interface NotifyService {

    /**
     * 根据通知模版和定义id获取通知列表，执行通知
     * 
     * @param notifyTemplateType
     *            通知模版类型
     * @param defineId
     *            定义id
     * @param alarmLevel
     *            报警级别，可空
     * @param params
     *            模版参数
     * @param notifyTeams
     *            是否通知teams
     * @param mailSupplier
     *            只有当包含邮件的时候才触发
     */
    void notifyByTemplate(NotifyTemplateTypeEnum notifyTemplateType, long defineId, AlarmLevelEnum alarmLevel,
        Map<String, Object> params, boolean notifyTeams, Supplier<MailExtraInfo> mailSupplier);

}
