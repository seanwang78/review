package com.uaepay.gateway.cgs.domain;

import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import lombok.Data;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/24
 * @since 0.1
 */
@Data
public class AppUpgradeQuery {

    /** 语言类型 */
    private String langType;
    /** 平台类型 */
    private PlatformType platformType;
    /** 当前版本号 */
    private String versionCode;
    /** sdk版本号 */
    private String sdkVersion;
    /** 载体 */
    private String vectorCode;
    /** 主机app渠道 */
    private String hostAppChannel;
}
