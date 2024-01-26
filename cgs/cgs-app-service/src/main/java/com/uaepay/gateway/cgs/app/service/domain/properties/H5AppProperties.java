package com.uaepay.gateway.cgs.app.service.domain.properties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "h5app")
public class H5AppProperties {

    // 检测Domain 开关
    boolean checkSwitch = true;

    /** 验证允许次数 */
    int verifyAllowCount = 2;

    /** 验证token有效时长 */
    Duration tokenTtl = Duration.ofMinutes(1);

    /** 允许域名列表 */
    Map<String, String> allowDomains = new HashMap<>();

}
