package com.payby.gateway.openapi.model.crypto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TerminalDetail implements Serializable {

    private static final long serialVersionUID = 129447L;
    private String operatorId;
    private String storeId;
    private String terminalId;
    private String merchantName;
    private String storeName;   
    private String location;

    @Override
    public String toString() {
        return "TerminalDetail(operatorId=" + this.operatorId + ", storeId=" + this.storeId + ", terminalId="
            + this.terminalId + ", storeName=" + this.storeName + ", merchantName=" + this.merchantName + ", location="
            + this.location + ")";
    }
}
