package com.uaepay.pub.csc.test.builder;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;

import com.uaepay.pub.csc.test.domain.CgsLogTestData;
import org.joda.time.DateTime;

public class CgsLogTestDataBuilder {

    private static final FastDateFormat SHARDING_FORMAT = FastDateFormat.getInstance("yyyyMMdd");

    CgsLogTestData result = new CgsLogTestData();

    public CgsLogTestDataBuilder(String appCode, String apiCode, String returnCode, String returnMsg) {
        result.setAppCode(appCode);
        result.setApiCode(apiCode);
        result.setReturnCode(returnCode);
        result.setReturnMsg(returnMsg);
    }

    public CgsLogTestDataBuilder tid(String tid) {
        result.setTid(tid);
        return this;
    }

    public CgsLogTestDataBuilder time(DateTime requestTime) {
        result.setRequestTime(requestTime.toDate());
        return this;
    }

    public CgsLogTestData build() {
        return result;
    }

    public String getSharding() {
        return SHARDING_FORMAT.format(result.getRequestTime());
    }

}
