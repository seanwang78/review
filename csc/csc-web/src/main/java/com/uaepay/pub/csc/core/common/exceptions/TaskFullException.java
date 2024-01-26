package com.uaepay.pub.csc.core.common.exceptions;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.pub.csc.service.facade.enums.CscReturnCode;

/**
 * @author zc
 */
public class TaskFullException extends FailException {

    public TaskFullException() {
        super(CscReturnCode.TASK_FULL);
    }

}
