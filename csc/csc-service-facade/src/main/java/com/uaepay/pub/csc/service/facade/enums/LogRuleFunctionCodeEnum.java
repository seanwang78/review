package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 监控类型枚举
 * 
 * @author caoyongxing
 */
public enum LogRuleFunctionCodeEnum implements CodeMessageEnum {

    /** 报表 */
    CGS_LOG_MONITOR("CGS_LOG_MONITOR", "cgs日志监控")
    ;

    private final String code;
    private final String message;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static LogRuleFunctionCodeEnum getByCode(String code) {
        for (LogRuleFunctionCodeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    LogRuleFunctionCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
