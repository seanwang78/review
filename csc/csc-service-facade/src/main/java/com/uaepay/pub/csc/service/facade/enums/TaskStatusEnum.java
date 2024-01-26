package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 对账任务状态枚举
 * 
 * @author zc
 */
public enum TaskStatusEnum implements CodeMessageEnum {

    /** 处理中 */
    PROCESS("P", "处理中"),

    /** 对账成功 */
    SUCCESS("S", "对账成功"),

    /** 对账异常 */
    ERROR("E", "对账异常", true),

    /** 对账失败 */
    FAIL("F", "对账失败", true),

    /** 补单处理中 */
    COMPENSATION_PROCESS("CP", "补单处理中", true),

    /** 等待重试 */
    RETRY_WAIT("RW", "等待重试", true),

    /** 重试成功 */
    RETRY_SUCCESS("RS", "重试成功"),

    /** 人工确认 */
    MANUAL_CONFIRMED("MC", "人工确认"),

    ;

    private final String code;
    private final String message;
    private final boolean manualConfirmable;

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static TaskStatusEnum getByCode(String code) {
        for (TaskStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    TaskStatusEnum(String code, String message) {
        this(code, message, false);
    }

    TaskStatusEnum(String code, String message, boolean manualConfirmable) {
        this.code = code;
        this.message = message;
        this.manualConfirmable = manualConfirmable;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public boolean isManualConfirmable() {
        return manualConfirmable;
    }

}
