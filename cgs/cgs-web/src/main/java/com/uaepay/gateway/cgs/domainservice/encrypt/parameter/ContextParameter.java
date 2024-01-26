package com.uaepay.gateway.cgs.domainservice.encrypt.parameter;

import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptParameter;
import lombok.Data;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2021/7/25
 * @since 0.1
 */
@Data
public class ContextParameter implements EncryptParameter {
    public ContextParameter(String salt, ReceiveOrderContext context) {
        this.salt = salt;
        this.context = context;
    }

    public ContextParameter(ReceiveOrderContext context) {
        this.context = context;
    }

    private String salt;

    private ReceiveOrderContext context;

}
