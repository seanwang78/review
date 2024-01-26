package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Protocol implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String merchantOrderNo;

    private String protocolNo;
    
    private String applySignStatus;
    
    private Date signTime;
    
    private Date effectTime;
    
    private Date invalidTime;
    
    private String protocolStatus;
    
    private String signerId;


}
