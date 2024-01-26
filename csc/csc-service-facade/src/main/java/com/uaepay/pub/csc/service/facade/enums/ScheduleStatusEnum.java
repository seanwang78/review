package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 计划状态枚举
 * 
 * @author zc
 */
public enum ScheduleStatusEnum implements CodeMessageEnum {

    /** 启用 */
    YES("Y", "启用"),

    /** 停用 */
    NO("N", "停用"),

    /** 异常停用 */
    ERROR("E", "异常停用"),

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
    public static ScheduleStatusEnum getByCode(String code) {
        for (ScheduleStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    ScheduleStatusEnum(String code, String message) {
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
