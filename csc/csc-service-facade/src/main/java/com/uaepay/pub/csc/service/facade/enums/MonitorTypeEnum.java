package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 监控类型枚举
 * 
 * @author zc
 */
public enum MonitorTypeEnum implements CodeMessageEnum {

    /** 报表 */
    REPORT("R", "报表"),

    /** 报警 */
    ALARM("A", "报警"),

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
    public static MonitorTypeEnum getByCode(String code) {
        for (MonitorTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    MonitorTypeEnum(String code, String message) {
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
