package com.uaepay.pub.csc.service.facade.enums;

import com.uaepay.basis.beacon.service.facade.enums.base.CodeMessageEnum;

/**
 * 对账任务类型枚举
 * 
 * @author zc
 */
public enum CscReturnCode implements CodeMessageEnum {

    /** 任务繁忙 */
    TASK_FULL("TASK_FULL", "Task full, please try again later..."),

    ;

    private final String code;

    private final String message;

    CscReturnCode(String code, String message) {
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
