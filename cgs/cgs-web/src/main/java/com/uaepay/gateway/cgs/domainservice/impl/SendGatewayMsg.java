/**   
 * Copyright © 2020 42PAY. All rights reserved.
 * 
 * @Title: SendResponseMsg.java 
 * @Prject: cgs-web
 * @Package: com.uaepay.gateway.cgs.domainservice.impl 
 * @author: heyang   
 * @date: Jan 13, 2020 3:12:22 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.domainservice.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uaepay.basis.beacon.common.util.JsonUtil;
import com.uaepay.gateway.cgs.app.template.ext.service.AbstracJmsSender;

/**
 * @ClassName: SendResponseMsg
 * @Description: 发送网关信息
 * @author: heyang
 * @date: Jan 13, 2020 3:12:22 PM
 */
@Component
public class SendGatewayMsg extends AbstracJmsSender<Object> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final String EXCHANGE = "uaepay.gateway.cgs.monitor.exchange";

	@Autowired
    private RabbitTemplate rabbitTemplate;
	
	@Override
	protected Logger getLogger() {
		return logger;
	}
	@Override
	protected String getQueueName() {
		return "uaepay.gateway.cgs.monitor.exchange";
	}
	/**
	 * @Title: sendResMsg 
	 * @Description: 发送信息
	 * @param content
	 * @return: void
	 */
	public void sendResMsg(Object content) {
		try {
			rabbitTemplate.convertAndSend(EXCHANGE, "", JsonUtil.toJsonString(content));
		} catch (AmqpException e) {
			logger.error("send JMS fail sendMsg is :{}",e);
		} catch (JsonProcessingException e) {
			logger.error("send JMS fail json is :{}",e);
		}
	}
	
}
