package com.uaepay.gateway.cgs.app.facade.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 刘智斌
 * 废弃 -- 不再维护
 * @version 0.1
 * @time 2019/11/19
 * @since 0.1
 */
public class Header implements Serializable {

    private static final long serialVersionUID = 1L;

    public static Header newHeader() {
        return new Header();
    }

    public Header(){}


    /**
     * 设备指纹
     */
    private String deviceId;

    /**
     * 网关记录的请求原始凭证号 可能为空
     *
     * 该字段是记录当次请求的凭证号
     * 交易和支付类接口建议记录
     * 如果配置了记录，则该字段不为空
     * 配置方法：
     *      1、新增接口到 t_gateway_api_config
     *      2、设置扩展字段 EXTENSION
     *      3、增加 recordVoucher（是否记录本次请求凭证 Y/N ）默认：N
     *      3、增加 voucherType （本次请求凭证类型）默认：CGS
     * @author Zhibin Liu
     */
    private String reqVoucherNo;

    /**
     * SDK版本号
     */
    private String sdkVersion;

    /**
     * 宿主程序
     */
    private String hostApp;

    /**
     * 宿主程序版本号
     */
    private String hostAppversion;

    /**
     * 基于零时区的毫秒偏移量
     */
    private int utcOffsetSeconds;

    /**
     * h5和web时有效
     */
    private String referer;

    /**
     * 盐值
     */
    private String saltKey;

    /**
     * 平台类型
     */
    private String platformType;

    /**
     * 内部版本号
     */
    private String versionCode;

    /**
     * 跳过升级
     */
    private String skipUpgrade;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * tmx session
     */
    private String tmxSession;

    /**
     * APP分包渠道
     */
    private String hostAppChannel;

    /** 透传给风控 */
    @ToStringExclude
    private Map<String,String> headerMaps;


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Header deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getReqVoucherNo() {
        return reqVoucherNo;
    }

    public void setReqVoucherNo(String reqVoucherNo) {
        this.reqVoucherNo = reqVoucherNo;
    }

    public Header reqVoucherNo(String reqVoucherNo) {
        this.reqVoucherNo = reqVoucherNo;
        return this;
    }

    public int getUtcOffsetSeconds() {
        return utcOffsetSeconds;
    }

    public Header utcOffsetSeconds(int utcOffsetSeconds) {
        this.utcOffsetSeconds = utcOffsetSeconds;
        return this;
    }

    /**
	 * @return the sdkVersion
	 */
	public String getSdkVersion() {
		return sdkVersion;
	}

	/**
	 * @param sdkVersion the sdkVersion to set
	 */
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	/**
	 * @return the hostApp
	 */
	public String getHostApp() {
		return hostApp;
	}

	/**
	 * @param hostApp the hostApp to set
	 */
	public void setHostApp(String hostApp) {
		this.hostApp = hostApp;
	}

	/**
	 * @return the hostAppversion
	 */
	public String getHostAppversion() {
		return hostAppversion;
	}

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    /**
	 * @param hostAppversion the hostAppversion to set
	 */
	public void setHostAppversion(String hostAppversion) {
		this.hostAppversion = hostAppversion;
	}

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSaltKey() {
        return saltKey;
    }

    public Header setSaltKey(String saltKey) {
        this.saltKey = saltKey;
        return this;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public Header setVersionCode(String versionCode) {
        this.versionCode = versionCode;
        return this;
    }

    public String getSkipUpgrade() {
        return skipUpgrade;
    }

    public Header setSkipUpgrade(String skipUpgrade) {
        this.skipUpgrade = skipUpgrade;
        return this;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getTmxSession() {
        return tmxSession;
    }

    public Header setTmxSession(String tmxSession) {
        this.tmxSession = tmxSession;
        return this;
    }

    public String getHostAppChannel() {
        return hostAppChannel;
    }

    public Header setHostAppChannel(String hostAppChannel) {
        this.hostAppChannel = hostAppChannel;
        return this;
    }

    public Map<String, String> getHeaderMaps() {
        return headerMaps;
    }

    public void setHeaderMaps(Map<String, String> headerMaps) {
        this.headerMaps = headerMaps;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
