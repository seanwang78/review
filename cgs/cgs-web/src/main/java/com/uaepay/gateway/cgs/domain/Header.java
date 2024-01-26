/**   
 * Copyright © 2020 42PAY. All rights reserved.
 * 
 * @Title: Header.java 
 * @Prject: cgs-web
 * @Package: com.uaepay.gateway.cgs.domain 
 * @author: heyang   
 * @date: Jan 13, 2020 8:20:38 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: Header
 * @Description: 头部信息
 * @author: heyang
 * @date: Jan 13, 2020 8:20:38 PM
 */
@Data
public class Header implements Serializable {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description:
	 */
	private static final long serialVersionUID = -6694604143155577324L;
	private String deviceId;
	private String sdkVersion;
	private String hostApp;
	private String hostAppversion;
	private Integer utcOffsetSeconds;
	
	public static class Builder implements Serializable{

		/**
		 * @fieldName: serialVersionUID
		 * @fieldType: long
		 * @Description: TODO
		 */
		private static final long serialVersionUID = 2399052509144667029L;
		private String deviceId;
		private String sdkVersion;
		private String hostApp;
		private String hostAppversion;
		private Integer utcOffsetSeconds;
		public Builder deviceId(String deviceId) {
			this.deviceId = deviceId;
			return this;
		}
		public Builder sdkVersion(String sdkVersion) {
			this.sdkVersion = sdkVersion;
			return this;
		}
		public Builder hostApp(String hostApp) {
			this.hostApp = hostApp;
			return this;
		}
		public Builder hostAppversion(String hostAppversion) {
			this.hostAppversion = hostAppversion;
			return this;
		}
		public Builder utcOffsetSeconds(Integer utcOffsetSeconds) {
			this.utcOffsetSeconds = utcOffsetSeconds;
			return this;
		}
		public Header builder() {
			Header header = new Header();
			header.setDeviceId(this.deviceId);
			header.setSdkVersion(this.sdkVersion);
			header.setHostApp(this.hostApp);
			header.setHostAppversion(this.hostAppversion);
			header.setUtcOffsetSeconds(this.utcOffsetSeconds);
			return header;
		}
	}
	public static Builder headerBuilder() {
		return new Builder();
	}
}
