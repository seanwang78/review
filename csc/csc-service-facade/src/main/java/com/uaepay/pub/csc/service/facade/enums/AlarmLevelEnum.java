package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 报警级别
 * 
 * @author zc
 */
public enum AlarmLevelEnum implements CodeMessageEnum {

    /** 忽略 */
    IGNORE("I", "忽略"),

    /** 普通通知 */
    NORMAL("N", "普通通知"),

    /** 紧急通知 */
    URGENT("U", "紧急通知"),

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
    public static AlarmLevelEnum getByCode(String code) {
        for (AlarmLevelEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    AlarmLevelEnum(String code, String message) {
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
