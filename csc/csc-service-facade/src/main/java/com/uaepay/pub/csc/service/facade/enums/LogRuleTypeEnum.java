package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 监控类型枚举
 * 
 * @author zc
 */
public enum LogRuleTypeEnum implements CodeMessageEnum {

    /** 黑名单 */
    BLACK("BLACK", "黑名单"),
    /**
     * 白名单
     */
    WHITE("WHITE", "白名单");

    private final String code;
    private final String message;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static LogRuleTypeEnum getByCode(String code) {
        for (LogRuleTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    LogRuleTypeEnum(String code, String message) {
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
