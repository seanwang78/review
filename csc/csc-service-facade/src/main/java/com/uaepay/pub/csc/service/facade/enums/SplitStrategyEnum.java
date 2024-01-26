package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 切分策略枚举
 * 
 * @author zc
 */
public enum SplitStrategyEnum implements CodeMessageEnum {

    /** 不切分 */
    NO("NO", "不切分"),

    UNION("UNION", "拼接"),
//
//    SUM("SUM", "汇总"),

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
    public static SplitStrategyEnum getByCode(String code) {
        for (SplitStrategyEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    SplitStrategyEnum(String code, String message) {
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
