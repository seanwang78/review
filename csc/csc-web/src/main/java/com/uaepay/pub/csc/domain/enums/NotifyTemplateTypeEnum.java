package com.uaepay.pub.csc.domain.enums;

import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;

/**
 * 通知模版类型枚举
 * 
 * @author zc
 */
public enum NotifyTemplateTypeEnum {

    /**
     * 监控报表
     */
    REPORT(DefineTypeEnum.MONITOR, NotifyStrategy.ONLY_EMAIL, "/META-INF/template/monitor_email_title.vm",
        "/META-INF/template/monitor_email_body.vm", null),

    /**
     * 监控报警
     */
    ALARM(DefineTypeEnum.MONITOR, NotifyStrategy.ALARM_LEVEL, "/META-INF/template/monitor_email_title.vm",
        "/META-INF/template/monitor_email_body.vm", "/META-INF/template/monitor_sms_body.vm"),

    /**
     * 运营对账
     */
    COMPARE(DefineTypeEnum.COMPARE, NotifyStrategy.ONLY_EMAIL, "/META-INF/template/compare_email_title.vm",
        "/META-INF/template/compare_email_body.vm", null),

    ;

    NotifyTemplateTypeEnum(DefineTypeEnum defineType, NotifyStrategy notifyStrategy, String emailTitle,
        String emailBody, String smsBody) {
        this.defineType = defineType;
        this.notifyStrategy = notifyStrategy;
        this.emailTitle = emailTitle;
        this.emailBody = emailBody;
        this.smsBody = smsBody;
    }

    final DefineTypeEnum defineType;

    final NotifyStrategy notifyStrategy;

    final String emailTitle;

    final String emailBody;

    final String smsBody;

    public DefineTypeEnum getDefineType() {
        return defineType;
    }

    public NotifyStrategy getNotifyStrategy() {
        return notifyStrategy;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public String getSmsBody() {
        return smsBody;
    }
}
