package com.uaepay.pub.csc.domainservice.notify.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.uaepay.common.util.VelocityUtil;
import com.uaepay.pub.csc.core.common.util.HttpUtil;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domain.notify.MailExtraInfo;
import com.uaepay.pub.csc.domainservice.notify.NotifyRepository;
import com.uaepay.pub.csc.domainservice.notify.NotifyService;
import com.uaepay.pub.csc.domainservice.notify.NotifyTemplateRepository;
import com.uaepay.pub.csc.ext.integration.MnsClient;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;
import com.uaepay.validate.exception.ValidationException;

/**
 * @author zc
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    Logger logger = LoggerFactory.getLogger(getClass());

    public NotifyServiceImpl(NotifyRepository notifyRepository, MnsClient mnsClient,
        NotifyTemplateRepository notifyTemplateRepository) {
        this.notifyRepository = notifyRepository;
        this.mnsClient = mnsClient;
        this.notifyTemplateRepository = notifyTemplateRepository;
    }

    NotifyRepository notifyRepository;

    MnsClient mnsClient;

    NotifyTemplateRepository notifyTemplateRepository;

    @Override
    public void notifyByTemplate(NotifyTemplateTypeEnum notifyTemplateType, long defineId, AlarmLevelEnum alarmLevel,
        Map<String, Object> params, boolean notifyTeams, final Supplier<MailExtraInfo> mailSupplier) {
        Map<NotifyTypeEnum, List<String>> contactGroup =
            notifyRepository.loadNotifyGroup(notifyTemplateType, defineId, alarmLevel);
        if (MapUtils.isEmpty(contactGroup)) {
            logger.info("无通知目标");
        } else {
            AtomicReference<MailExtraInfo> mailExtraRef = new AtomicReference<>();
            contactGroup.forEach((notifyType, notifyIdentityList) -> {
                logger.info("通知方式: {}，通知列表: {}", notifyType, notifyIdentityList.stream()
                    .map(d -> StringUtils.abbreviateMiddle(d, "*", 10)).collect(Collectors.toList()));
                if (notifyType == NotifyTypeEnum.EMAIL) {
                    notifyEmail(notifyTemplateType, params, notifyIdentityList,
                        getMailExtra(mailSupplier, mailExtraRef));
                } else if (notifyType == NotifyTypeEnum.SMS) {
                    notifySMS(notifyTemplateType, params, notifyIdentityList);
                }
            });
        }

        // 如果不是监控统计就发送teams通知
        if (notifyTeams) {
            // 发送teams频道通知
            Map<String, String> teamsNotifyUrlList = notifyRepository.getTeamsUrlList(notifyTemplateType, defineId);
            if (teamsNotifyUrlList.size() == 0) {
                logger.info("Teams频道无通知目标");
            } else {
                for (String key : teamsNotifyUrlList.keySet()) {
                    logger.info("通知方式: teams频道通知，通知组: {}", key);
                    notifyTeams(notifyTemplateType, params, teamsNotifyUrlList.get(key));
                }
            }
        }
    }

    private void notifyEmail(NotifyTemplateTypeEnum templateType, Map<String, Object> params,
        List<String> notifyIdentityList, MailExtraInfo extraInfo) {
        if (StringUtils.isBlank(templateType.getEmailTitle()) || StringUtils.isBlank(templateType.getEmailBody())) {
            logger.info("未配置邮件模版");
            return;
        }
        try {
            String title =
                VelocityUtil.getString(notifyTemplateRepository.loadTemplate(templateType.getEmailTitle()), params);
            String body =
                VelocityUtil.getString(notifyTemplateRepository.loadTemplate(templateType.getEmailBody()), params);
            mnsClient.sendMail(notifyIdentityList, title, body, extraInfo);
        } catch (ValidationException e) {
            logger.error("邮件模版异常", e);
        }
    }

    private void notifySMS(NotifyTemplateTypeEnum templateType, Map<String, Object> params,
        List<String> notifyIdentityList) {
        if (StringUtils.isBlank(templateType.getSmsBody())) {
            logger.info("未配置短信模版");
            return;
        }
        try {
            String body =
                VelocityUtil.getString(notifyTemplateRepository.loadTemplate(templateType.getSmsBody()), params);
            mnsClient.sendSMS(notifyIdentityList, body);
        } catch (ValidationException e) {
            logger.error("短信模版异常", e);
        }
    }

    private void notifyTeams(NotifyTemplateTypeEnum templateType, Map<String, Object> params, String url) {
        if (StringUtils.isBlank(templateType.getEmailTitle()) || StringUtils.isBlank(templateType.getEmailBody())) {
            logger.info("未配置邮件模版");
            return;
        }
        try {
            String teamsNotifyInfo = notifyTemplateRepository.buildTeamsNotifyInfo(templateType, params);
            HttpUtil.httpPost(teamsNotifyInfo, url);
            // String teamsNotifyTemplate =
            // notifyTemplateRepository.loadTemplate(notifyTemplateRepository.loadTeamsNotifyHead());
            // String title =
            // VelocityUtil.getString(notifyTemplateRepository.loadTemplate(templateType.getEmailTitle()), params);
            // String body = VelocityUtil.getString(notifyTemplateRepository.loadTemplate(templateType.getEmailBody()),
            // params);

        } catch (Exception e) {
            logger.error("Teams通知模版异常", e);
        }
    }

    /**
     * 邮件扩展参数
     * 
     * @param mailSupplier
     *            邮件额外参数Supplier
     * @param mailExtraRef
     *            邮件额外参数引用
     */
    private MailExtraInfo getMailExtra(Supplier<MailExtraInfo> mailSupplier,
        AtomicReference<MailExtraInfo> mailExtraRef) {
        MailExtraInfo extraInfo = mailExtraRef.get();
        if (extraInfo == null) {
            extraInfo = mailSupplier != null ? mailSupplier.get() : null;
            mailExtraRef.set(extraInfo);
        }
        return extraInfo;
    }
}
