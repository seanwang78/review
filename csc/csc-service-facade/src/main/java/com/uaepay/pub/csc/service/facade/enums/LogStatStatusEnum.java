package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 监控类型枚举
 * 
 * @author caoyongxing
 */
public enum LogStatStatusEnum implements CodeMessageEnum {

    /**
     * 初始
     */
    INIT("I", "初始状态"),
    MANUAL("M", "人工确认"),
    RETRY("R", "重试成功");

    private final String code;
    private final String message;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static LogStatStatusEnum getByCode(String code) {
        for (LogStatStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    LogStatStatusEnum(String code, String message) {
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
