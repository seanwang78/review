package com.uaepay.gateway.cgs.constants;

import java.nio.charset.Charset;

/**
 * 网关参数常量
 */
public interface GatewayConstants {

    String LANG = "Content-Language";

    String ACCESS_TOKEN = "Access-Token";

    /** 新token */
    String TOKEN = "Token";

    String SIGN = "sign";

    String PLATFORM = "platform";

    /** 设备指纹 */
    String DEVICE_FINGERPRINT = "Device-Id";

    /** sdk版本号 */
    String SDKVERSIONS = "sdkVersions";

    /** sdk宿主 */
    String HOST_APP = "Host-App";

    /** TMX会话，风控第三方SDK Session */
    String TMX_SESSION = "Tmx-Session";

    /** sdk宿主版本 */
    String HOST_APP_VERSION = "Host-App-Version";
    /** 宿主渠道 */
    String HOST_APP_CHANNEL = "Host-App-Channel";

    String UTCOFFSET_SECONDS = "Utc-Offset-Seconds";

    /** 内部版本号 */
    String VERSION_CODE = "Version-Code";

    /** 跳过升级 */
    String SKIP_UPGRADE = "Skip-Upgrade";

    /** referer */
    String REFERER = "referer";

    /** 默认参数编码字符集 */
    String DEFAULT_CHARSET_NAME = "UTF-8";

    /** 默认参数编码字符集 */
    Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

    String TRACE_ID = "trace-id";

    String SALT_KEY = "salt-key";

    String BUSINESS_TAGS = "business-tags";
}
