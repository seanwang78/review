/**   
 * Copyright © 2020 42PAY. All rights reserved.
 * 
 * @Title: TmsDomain.java 
 * @Prject: cgs-web
 * @Package: com.uaepay.gateway.cgs.domain 
 * @author: heyang   
 * @date: Jan 13, 2020 4:18:30 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/** 
 * @ClassName: TmsDomain 
 * @Description: 发送es网关消息
 * @author: heyang
 * @date: Jan 13, 2020 4:18:30 PM  
 */
@Data
public class TmsDomain implements Serializable{
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description:
	 */
	private static final long serialVersionUID = 3934453006336991502L;
	private String tid;
	private String systemName;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
	private Date requestTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
	private Date responseTime;
	private Long totalTime;
	private String lang;
	private String platformType;
	private String appCode;
	private String apiType;
	private String apiCode;
	private String memberId;
	private String identity;
	private String partnerId;
	private String returnCode;
	private String returnMsg;
	private String clientIp;
	private Header header;
	
	public static class Builder implements Serializable{
		/**
		 * @fieldName: serialVersionUID
		 * @fieldType: long
		 * @Description:
		 */
		private static final long serialVersionUID = 6043259714707225217L;
		private String tid;
		private String systemName;
		private Date requestTime;
		private Date ResponseTime;
		private Long totalTime;
		private String lang;
		private String platformType;
		private String appCode;
		private String apiType;
		private String apiCode;
		private String memberId;
		private String identity;
		private String partnerId;
		private String returnCode;
		private String returnMsg;
		private String clientIp;
		private Header header;
		
		public Builder tid(String tid) {
            this.tid = tid;
            return this;
        }
		public Builder systemName(String systemName) {
			this.systemName = systemName;
			return this;
		}
		public Builder setRequestTime(Date requestTime) {
			this.requestTime = requestTime;
			return this;
		}
		public Builder setResponseTime(Date responseTime) {
			ResponseTime = responseTime;
			return this;
		}
		public Builder setTotalTime(Long totalTime) {
			this.totalTime = totalTime;
			return this;
		}
		public Builder setLang(String lang) {
			this.lang = lang;
			return this;
		}
		public Builder setPlatformType(String platformType) {
			this.platformType = platformType;
			return this;
		}
		public Builder setAppCode(String appCode) {
			this.appCode = appCode;
			return this;
		}
		public Builder setApiType(String apiType) {
			this.apiType = apiType;
			return this;
		}
		public Builder setApiCode(String apiCode) {
			this.apiCode = apiCode;
			return this;
		}
		public Builder setMemberId(String memberId) {
			this.memberId = memberId;
			return this;
		}
		public Builder setIdentity(String identity) {
			this.identity = identity;
			return this;
		}
		public Builder setPartnerId(String partnerId) {
			this.partnerId = partnerId;
			return this;
		}
		public Builder setReturnCode(String returnCode) {
			this.returnCode = returnCode;
			return this;
		}
		public Builder setReturnMsg(String returnMsg) {
			this.returnMsg = returnMsg;
			return this;
		}
		public Builder setClientIp(String clientIp) {
			this.clientIp = clientIp;
			return this;
		}
		public Builder setHeader(Header header) {
			this.header = header;
			return this;
		}
		public TmsDomain builder() {
			TmsDomain tmsDomain = new TmsDomain();
			tmsDomain.setApiType(this.apiType);
			tmsDomain.setTid(this.tid);
			tmsDomain.setSystemName(this.systemName);
			tmsDomain.setRequestTime(this.requestTime);
			tmsDomain.setResponseTime(this.ResponseTime);
			tmsDomain.setTotalTime(this.totalTime);
			tmsDomain.setLang(this.lang);
			tmsDomain.setPlatformType(this.platformType);
			tmsDomain.setAppCode(this.appCode);
			tmsDomain.setApiType(this.apiType);
			tmsDomain.setApiCode(this.apiCode);
			tmsDomain.setMemberId(this.memberId);
			tmsDomain.setIdentity(this.identity);
			tmsDomain.setPartnerId(this.partnerId);
			tmsDomain.setReturnCode(this.returnCode);
			tmsDomain.setReturnMsg(this.returnMsg);
			tmsDomain.setClientIp(this.clientIp);
			tmsDomain.setHeader(this.header);
			return tmsDomain;
		}
		
	}
	public static Builder tmsBuilder() {
		return new Builder();
	}
}
