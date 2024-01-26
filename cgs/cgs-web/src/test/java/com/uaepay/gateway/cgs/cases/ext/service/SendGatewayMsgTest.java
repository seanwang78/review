/**   
 * Copyright © 2020 42PAY. All rights reserved.
 * 
 * @Title: SendGatewayMsgTest.java 
 * @Prject: cgs-web
 * @Package: com.uaepay.gateway.cgs.cases.ext.service 
 * @author: heyang   
 * @date: Jan 13, 2020 3:25:55 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.cases.ext.service;

import com.uaepay.gateway.cgs.domainservice.impl.SendGatewayMsg;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** 
 * @ClassName: SendGatewayMsgTest 
 * @Description:
 * @author: heyang
 * @date: Jan 13, 2020 3:25:55 PM  
 */
public class SendGatewayMsgTest extends ApplicationTestBase{
	@Autowired
	private SendGatewayMsg SendGatewayMsg;
	
	@Test
	public void sendmsg() {
		SendGatewayMsg.sendResMsg("eeggg");
	}
}
