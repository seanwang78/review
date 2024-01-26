package com.uaepay.gateway.cgs.app.template.testtool.common;

import com.uaepay.gateway.cgs.app.facade.domain.Header;

/**
 * 业务Header 构建类 .
 * <p>
 *
 * @author yusai
 * @date 2020-06-14 18:08.
 */
public class HeaderBuilder {

    public HeaderBuilder builder() {
        return new HeaderBuilder();
    }

    Header header = new Header();

    public HeaderBuilder deviceId(String deviceId){
        header.setDeviceId(deviceId);
        return this;
    }

    public HeaderBuilder reqVoucherNo(String reqVoucherNo){
        header.setReqVoucherNo(reqVoucherNo);
        return this;
    }

    public HeaderBuilder sdkVersion(String sdkVersion){
        header.setSdkVersion(sdkVersion);
        return this;
    }

    public HeaderBuilder hostApp(String hostApp){
        header.setHostApp(hostApp);
        return this;
    }


    public HeaderBuilder hostAppversion(String hostAppversion){
        header.setHostAppversion(hostAppversion);
        return this;
    }

    public HeaderBuilder utcOffsetSeconds(int utcOffsetSeconds){
        header.utcOffsetSeconds(utcOffsetSeconds);
        return this;
    }

    public HeaderBuilder referer(String referer){
        header.setReferer(referer);
        return this;
    }

    public HeaderBuilder saltKey(String saltKey){
        header.setSaltKey(saltKey);
        return this;
    }

    public HeaderBuilder platformType(String platformType){
        header.setPlatformType(platformType);
        return this;
    }

    public HeaderBuilder versionCode(String versionCode){
        header.setVersionCode(versionCode);
        return this;
    }

    public HeaderBuilder skipUpgrade(String skipUpgrade){
        header.setSkipUpgrade(skipUpgrade);
        return this;
    }

    public HeaderBuilder clientIp(String clientIp){
        header.setClientIp(clientIp);
        return this;
    }

    public HeaderBuilder tmxSession(String tmxSession){
        header.setTmxSession(tmxSession);
        return this;
    }

    public Header build() {
        return header;
    }


}
