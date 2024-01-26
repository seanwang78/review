package com.uaepay.gateway.cgs.constants;

/**
 * 自定义Http Header key .
 * <p>
 * sdk 2.0 后重新按标准定义key
 * http://wiki.test2pay.com/pages/viewpage.action?pageId=15238006
 * @author yusai
 * @date 2020-08-08 10:54.
 */
public interface HttpHeaderKey {

    String X_PLATFORM = "X-Platform";

    String X_DEVICEID = "X-Device-Id";

    String X_SDKVERSION = "X-Sdk-Version";

    String X_TZOFFSET = "X-TZ-Offset";

    String X_HOSTAPP = "X-Host-App";

    String X_HOSTAPPVERSION = "X-Host-App-Version";

    String X_SALT = "X-Salt";

    String X_VERSIONCODE = "X-Version-Code";

    String X_TMXSESSION	 = "X-Tmx-Session";

    String X_ALISESSION = "X-Ali-Session";

    String X_HOSTAPPCHANNEL	 = "X-Host-App-Channel";

    String X_ACCESSTOKEN = "X-Access-Token";

    String X_TOKEN = "X-Token";

    String X_SIGN = "X-Sign";

    String X_GUARDTOKEN  = "X-Guard-Token";

    String X_UCID = "X-ucid";

    String X_ACCOUNTID = "X-Account-Id";
}
