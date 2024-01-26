package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 优先级枚举
 * 
 * @author zc
 */
public enum PriorityEnum implements CodeMessageEnum {

    /** 高 */
    HIGH("0", "高"),

    /** 中 */
    MIDDLE("2", "中"),

    /** 低 */
    LOW("4", "低"),

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
    public static PriorityEnum getByCode(String code) {
        for (PriorityEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    PriorityEnum(String code, String message) {
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
