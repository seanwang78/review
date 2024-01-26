package com.uaepay.pub.csc.ext.integration;

import java.util.List;

import com.uaepay.pub.csc.domain.notify.MailExtraInfo;

/**
 * 消息通知客户端
 * 
 * @author zc
 */
public interface MnsClient {

    void sendMail(List<String> mailList, String subject, String content, MailExtraInfo extraInfo);

    void sendSMS(List<String> mobileList, String content);

}
