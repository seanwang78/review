package com.uaepay.pub.csc.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 通知模版配置
 * 
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "notify.template")
public class NotifyTemplateProperties {

    /**
     * 邮件主题
     */
    String mailSubject;

}
