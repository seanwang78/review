package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 对账任务状态枚举
 * 
 * @author zc
 */
public enum MonitorTaskStatusEnum implements CodeMessageEnum {

    /** 处理中 */
    PROCESS("P", "处理中"),

    /** 无报警 */
    SUCCESS("S", "成功"),

    /** 异常 */
    ERROR("E", "异常"),

    /** 报警 */
    FAIL("F", "失败"),

    /** 重试成功 */
    RETRY_SUCCESS("RS", "重试成功"),

    /** 人工确认 */
    MANUAL_CONFIRMED("MC", "人工确认"),

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
    public static MonitorTaskStatusEnum getByCode(String code) {
        for (MonitorTaskStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    MonitorTaskStatusEnum(String code, String message) {
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
