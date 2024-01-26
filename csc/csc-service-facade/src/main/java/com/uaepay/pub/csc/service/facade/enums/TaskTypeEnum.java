package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 任务类型枚举
 * 
 * @author zc
 */
public enum TaskTypeEnum implements CodeMessageEnum {

    /** 人工 */
    MANUAL("M", "人工"),

    /** 计划 */
    SCHEDULE("S", "计划"),

    /** 重试 */
    RETRY("R", "重试"),

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
    public static TaskTypeEnum getByCode(String code) {
        for (TaskTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    TaskTypeEnum(String code, String message) {
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
