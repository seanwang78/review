package com.uaepay.pub.csc.domain.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeEnum;

/**
 * 任务异常代码
 * 
 * @author zc
 */
public enum TaskErrorCodeEnum implements CodeEnum {

    /** SQL错误 */
    BAD_SQL("BAD_SQL"),

    /** 校验表达式错误 */
    CHECK_EXPRESSION_ERROR("CHECK_EXPRESSION_ERROR"),

    /** 补单表达式错误 */
    COMPENSATE_EXPRESSION_ERROR("COMPENSATE_EXPRESSION_ERROR"),

    /** 通知表达式错误 */
    NOTIFY_EXPRESSION_ERROR("NOTIFY_EXPRESSION_ERROR"),

    ;

    private final String code;

    TaskErrorCodeEnum(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }

    /**
     * 根据编码获取枚举
     *
     * @param code
     *            编码
     * @return 对应枚举类型
     */
    public static TaskErrorCodeEnum getByCode(String code) {
        for (TaskErrorCodeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
