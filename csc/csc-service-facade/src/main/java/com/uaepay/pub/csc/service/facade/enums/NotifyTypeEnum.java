package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 通知类型
 * 
 * @author zc
 */
public enum NotifyTypeEnum implements CodeMessageEnum {

    /** 邮箱 */
    EMAIL("EMAIL", "邮箱"),

    /** 短信 */
    SMS("SMS", "短信"),

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
    public static NotifyTypeEnum getByCode(String code) {
        for (NotifyTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    NotifyTypeEnum(String code, String message) {
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
