package com.uaepay.pub.csc.domain.compensate;

import lombok.Data;

/**
 * 补单配置
 * @author zc
 */
@Data
public class CompensateConfig {

    /**
     * 应用代码，dubbo服务版本，必须
     */
    String appCode;

    /**
     * 通知类型，必须
     */
    String notifyType;

}
