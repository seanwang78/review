package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 比对状态枚举
 * 
 * @author zc
 */
public enum CompareStatusEnum implements CodeMessageEnum {

    /** 少数据 */
    LACK("L", "少数据"),

    /** 不匹配 */
    MISMATCH("M", "不匹配"),

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
    public static CompareStatusEnum getByCode(String code) {
        for (CompareStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    CompareStatusEnum(String code, String message) {
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
