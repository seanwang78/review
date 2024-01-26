package com.uaepay.pub.csc.test.domain;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CgsLogTestData {

    /**
     * "tid" : "20169.94.16410944397468909", "systemName" : "CGS", "requestTime" : "2022-01-02T03:33:59.747+0000",
     * "responseTime" : "2022-01-02T03:33:59.768+0000", "totalTime" : 21, "lang" : "en", "platformType" : "H5",
     * "appCode" : "gp064_customer-frontend", "apiType" : "UNAUTHED", "apiCode" :
     * "/customerFrontend/store/queryEatmStoreDistancePage", "returnCode" : "200", "returnMsg" : "apply success",
     * "clientIp" : "94.203.230.118", "header" : { "hostApp" : "totok-pay", "utcOffsetSeconds" : 14400 }
     */
    private String tid;
    private String systemName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone="GMT+4")
    private Date requestTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ",timezone="GMT+4")
    private Date responseTime;
    private String totalTime;
    private String lang;
    private String platformType;
    private String appCode;
    private String apiType;
    private String apiCode;
    private String returnCode;
    private String returnMsg;
    private String clientIp;
    private Map header;
}
