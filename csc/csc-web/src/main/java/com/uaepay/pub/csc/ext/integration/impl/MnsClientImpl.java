package com.uaepay.pub.csc.ext.integration.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.mns.service.facade.MsgNotifyFacade;
import com.uaepay.mns.service.facade.enums.NotifyProtocol;
import com.uaepay.mns.service.facade.request.MsgNotifyRequest;
import com.uaepay.mns.service.facade.response.MsgNotifyResult;
import com.uaepay.pub.csc.domain.notify.MailExtraInfo;
import com.uaepay.pub.csc.ext.integration.MnsClient;
import com.uaepay.pub.csc.ext.integration.base.BaseClient;

/**
 * mns通知服务
 * 
 * @author zc
 */
@Service
public class MnsClientImpl extends BaseClient implements MnsClient {

    static AtomicLong COUNT = new AtomicLong(995);

    @Reference
    private MsgNotifyFacade msgNotifyFacade;

    @Override
    public void sendMail(List<String> mailList, String subject, String content, MailExtraInfo extraInfo) {
        MsgNotifyRequest request = new MsgNotifyRequest();
        request.setMsgOrderNo(buildOrderNo());
        request.setSubject(subject);
        request.setContent(content);
        request.setTargetIdenty(StringUtils.join(mailList, ','));
        request.setTargetCount(mailList.size());
        request.setAppId(clientId);
        request.setNotifyProtocol(NotifyProtocol.MAIL);
        request.setPriority(0);
        if (extraInfo != null) {
            if (CollectionUtils.isNotEmpty(extraInfo.getAttachments())) {
                request.setAttachmentsJson(JSONObject.toJSONString(extraInfo.getAttachments()));
            }
        }
        logger.info("mns邮件通知请求: {}", request.getMsgOrderNo());
        MsgNotifyResult response = msgNotifyFacade.applyNotify(request);
        logger.info("mns邮件通知响应: {}", response);
    }

    @Override
    public void sendSMS(List<String> mobileList, String content) {
        MsgNotifyRequest request = new MsgNotifyRequest();
        request.setMsgOrderNo(buildOrderNo());
        request.setContent(content);
        request.setTargetIdenty(StringUtils.join(mobileList, ','));
        request.setTargetCount(mobileList.size());
        request.setAppId(clientId);
        request.setNotifyProtocol(NotifyProtocol.SNS);
        request.setPriority(0);
        logger.info("mns短信通知请求: {}", request.getMsgOrderNo());
        MsgNotifyResult response = msgNotifyFacade.applyNotify(request);
        logger.info("mns短信通知响应: {}", response);
    }

    private static String buildOrderNo() {
        return String.format("CSC%s%03d", new DateTime(new Date()).toString("yyyyMMddHHmmss"), COUNT.incrementAndGet() % 1000);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i != 10; i++) {
            Thread.sleep(1000);
            System.out.println(buildOrderNo());
        }
    }

}
