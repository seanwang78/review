package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 补单标志
 * 
 * @author zc
 */
public enum CompensateFlagEnum implements CodeMessageEnum {

    /** 等待通知 */
    WAIT("W", "等待通知"),

    /** 通知成功 */
    SUCCESS("S", "通知成功"),

    /** 通知失败 */
    FAIL("F", "通知失败"),

    /** 通知异常 */
    ERROR("E", "通知异常"),

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
    public static CompensateFlagEnum getByCode(String code) {
        for (CompensateFlagEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    CompensateFlagEnum(String code, String message) {
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
