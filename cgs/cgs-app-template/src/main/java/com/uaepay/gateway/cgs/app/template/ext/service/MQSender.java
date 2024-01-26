/**   
 * Copyright © 2020 42PAY. All rights reserved.
 * 
 * @Title: MQSender.java 
 * @Prject: cgs-app-template
 * @Package: com.uaepay.gateway.cgs.app.template.ext.service 
 * @author: heyang   
 * @date: Jan 13, 2020 2:51:33 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.app.template.ext.service;

/** 
 * @ClassName: MQSender 
 * @Description: 消息队列发送接口
 * @author: heyang
 * @date: Jan 13, 2020 2:51:33 PM  
 */
public interface MQSender<T> {
	/**
	 * @Title: send 
	 * @Description: 发送消息
	 * @param content 发送内容
	 * @return: Boolean
	 */
	Boolean send(T content);
}
