package com.uaepay.pub.csc.test.checker.mns;

import org.springframework.stereotype.Component;

import com.uaepay.mns.service.facade.response.MsgNotifyResult;

import lombok.Getter;

/**
 * @author lzb
 */
@Component
public class MnsApplyNotifyMocker {

    @Getter
    public MsgNotifyResult result;

    public void fixSuccess() {
        result = new MsgNotifyResult();
        result.setReturnCode("0000");
        result.setReturnMsg("mock result");
    }

    public void fixFail() {
        result = new MsgNotifyResult();
        result.setReturnCode("0001");
        result.setReturnMsg("mock result");
    }

}
