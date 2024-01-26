package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 定义类型
 * 
 * @author zc
 */
public enum DefineTypeEnum implements CodeMessageEnum {

    /** 运营对账 */
    COMPARE("C", "运营对账"),

    /** 运营监控 */
    MONITOR("M", "运营监控"),

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
    public static DefineTypeEnum getByCode(String code) {
        for (DefineTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    DefineTypeEnum(String code, String message) {
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
