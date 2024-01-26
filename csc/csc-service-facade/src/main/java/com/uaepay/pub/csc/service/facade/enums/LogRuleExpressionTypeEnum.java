package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 监控类型枚举
 * 
 * @author zc
 */
public enum LogRuleExpressionTypeEnum implements CodeMessageEnum {

    /** 全文匹配 */
    TEXT("TEXT", "全文"),
    /** 正则 */
    REGEX("REGEX", "正则"),
    /**
     * 不需匹配
     */
    NONE("NONE", "不需匹配");

    private final String code;
    private final String message;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static LogRuleExpressionTypeEnum getByCode(String code) {
        for (LogRuleExpressionTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    LogRuleExpressionTypeEnum(String code, String message) {
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
