/**   
 * Copyright © 2020 42PAY. All rights reserved.
 * 
 * @Title: AbstracJmssender.java 
 * @Prject: cgs-app-template
 * @Package: com.uaepay.gateway.cgs.app.template.ext.service 
 * @author: heyang   
 * @date: Jan 13, 2020 2:48:36 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.app.template.ext.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.mq.constant.DeliveryMode;
import com.uaepay.mq.constant.MessageFormat;
import com.uaepay.mq.core.MQException;
import com.uaepay.mq.core.MQService;
import com.uaepay.mq.request.notify.DefaultNotifyRequest;

/**
 * @ClassName: AbstracJmssender
 * @Description: 消息抽象类
 * @author: heyang
 * @date: Jan 13, 2020 2:48:36 PM
 */
public abstract class AbstracJmsSender<T> implements MQSender<T> {
	abstract protected Logger getLogger();

	/** MQ消息服务 */
	@Autowired
	private MQService mqService;

	/*
	 * (non Javadoc)
	 * @Title: send
	 * @Description: 发送消息接口
	 * @param content
	 * @return
	 * @see com.uaepay.gateway.cgs.app.template.ext.service.MQSender#send(java.lang.Object)
	 */
	@Override
	public Boolean send(T content) {
		try {
			DefaultNotifyRequest<T> notifyRequest = new DefaultNotifyRequest<T>();
			notifyRequest.setContent(content);
			notifyRequest.setTransacted(true);
			notifyRequest.setDestination(getQueueName());
			notifyRequest.setMessageFormat(MessageFormat.JSON);
			notifyRequest.setDeliveryMode(DeliveryMode.PERSISTENT);
			return this.send(notifyRequest);
		} catch (Exception e) {
			getLogger().error("发送JMS消息未知异常", e);
			return false;
		}
	}
	public Boolean send(DefaultNotifyRequest<T> notifyRequest) {
		try {
			mqService.sendMessage(notifyRequest);
			return true;
		} catch (MQException e) {
			getLogger().error("发送JMS消息异常", e);
			return false;
		} catch (Exception e) {
			getLogger().error("发送JMS消息未知异常", e);
			return false;
		}
	}
	protected abstract String getQueueName();
	
	public void setMqService(MQService mqService) {
		this.mqService = mqService;
	}
}
