package com.uaepay.pub.csc.domainservice.compare.task.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.uaepay.common.util.VelocityUtil;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.properties.NotifyTemplateProperties;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskNotifyService;
import com.uaepay.pub.csc.ext.integration.MnsClient;
import com.uaepay.pub.csc.service.facade.enums.TaskStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.TaskTypeEnum;
import com.uaepay.validate.exception.ValidationException;

/**
 * 通知服务
 * 
 * @author zc
 */
@Service
public class CompareNotifyServiceImpl implements CompareTaskNotifyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompareNotifyServiceImpl.class);

    private static final String NOTIFY_TEMPLATE_PATH = "/META-INF/template/compare_fail_mail.vm";

    private static final String NOTIFY_PARAM_TASK = "task";
    private static final String NOTIFY_PARAM_DEFINE = "define";
    private static final String NOTIFY_PARAM_RESULT = "result";
    private static final String NOTIFY_PARAM_UTIL = "util";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static String COMPARE_FAIL_MAIL_TEMPLATE;

    @Autowired
    MnsClient mnsClient;

    @Autowired
    NotifyTemplateProperties notifyTemplateProperties;

    @Override
    public String notifyCompareResult(CompareTask task, CompareDefine define, CompareResult result) {
        // 人工对账不通知 或 任务非失败不通知
        if (task.getTaskType() == TaskTypeEnum.MANUAL || task.getTaskStatus() != TaskStatusEnum.FAIL) {
            return null;
        }
        // 没有异常明细不通知
        if (result.getErrorCount() == 0) {
            return null;
        }

        Map<String, Object> params = new HashMap<>(3);
        params.put(NOTIFY_PARAM_TASK, task);
        params.put(NOTIFY_PARAM_DEFINE, define);
        params.put(NOTIFY_PARAM_RESULT, result);
        params.put(NOTIFY_PARAM_UTIL, new Util(task));

        String subject, template;
        try {
            subject = VelocityUtil.getString(notifyTemplateProperties.getMailSubject(), params);
            template = VelocityUtil.getString(getContentTemplate(), params);
        } catch (ValidationException e) {
            LOGGER.error("邮件模版异常", e);
            return null;
        }

        String orderNo = UUID.randomUUID().toString().replace("-", "");
        List<String> mailAddress = Arrays.asList("cong.zhou@42pay.com");

//        mnsClient.sendMail(orderNo, mailAddress, subject, template);
        return orderNo;
    }

    private String getContentTemplate() {
        if (COMPARE_FAIL_MAIL_TEMPLATE != null) {
            return COMPARE_FAIL_MAIL_TEMPLATE;
        }
        try {
            File file = new ClassPathResource(NOTIFY_TEMPLATE_PATH).getFile();
            COMPARE_FAIL_MAIL_TEMPLATE = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return COMPARE_FAIL_MAIL_TEMPLATE;
    }

    public class Util {

        public Util(CompareTask task) {
            this.task = task;
        }

        CompareTask task;

        public String dataBeginEndTime() {
            return dataBeginTime() + "~" + dataEndTime();
        }

        public String dataBeginTime() {
            return DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(task.getDataBeginTime());
        }

        public String dataEndTime() {
            return DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(task.getDataEndTime());
        }

    }
}
