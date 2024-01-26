package com.uaepay.gateway.cgs.domainservice.impl;

import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.common.CommonUtils;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domainservice.ConsultRiskService;
import com.uaepay.gateway.cgs.integration.cps.CpsService;
import com.uaepay.gateway.cgs.integration.member.MemberClient;
import com.uaepay.rm.cps.stub.cpsstub.vo.CgsGatewayLegalityCheckParam;
import com.uaepay.rm.cps.stub.cpsstub.vo.CgsGatewayLegalityCheckParam.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 风控实现类
 * @author heyang
 * @date Dec 26, 2019 2:19:10 PM
 */
@Service
public class ConsultRiskServiceImpl implements ConsultRiskService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	CpsService cpsService;

	@Autowired
	MemberClient memberClient;

	@Override
	@Async("asyncServiceExecutor")
	public void riskBuriedPoint(ReceiveOrderContext context) {
		if (context.getApiConfig().isAuthed()) {
			// 授权接口才 push 风控
			AccessMember accessMember = context.getAccessMember();
			HttpServletRequest httpRequest = context.getHttpRequest();
			Header header = context.getHeader();
			String ip = CommonUtils.getIpAddress(httpRequest);
			cpsService.cgsGatewayLegalityCheck(CgsGatewayLegalityCheckParam.paramBuilder()
					.platform(getPlatform(context)).memberId(accessMember.getMemberId())
					.mobile(accessMember.getMobile()).deviceId(context.getHeader().getDeviceId())
					.sdkVersion(header.getSdkVersion()).hostApp(header.getHostApp())
					.hostAppVersion(header.getHostAppversion()).timestamp(System.currentTimeMillis())
					.serviceUrl(context.getApiConfig().getApiCode()).serviceVersion(context.getApiConfig().getAppCode())
					.language(context.getLang().getCode()).ip(ip).build());
		}
	}

	private Platform getPlatform(ReceiveOrderContext context) {
		String code = context.getPlatformType().getCode();
		switch (code) {
			case "1":
				return CgsGatewayLegalityCheckParam.Platform.IOS;
			case "2":
				return CgsGatewayLegalityCheckParam.Platform.Android;
			case "3":
				return CgsGatewayLegalityCheckParam.Platform.Web;
			case "4":
				return CgsGatewayLegalityCheckParam.Platform.H5;
			default:
				return CgsGatewayLegalityCheckParam.Platform.IOS;
		}
	}
}
