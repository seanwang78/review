package com.uaepay.pub.csc.test.base;

import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;

public class TestBase {

    protected static final String CLIENT_ID = "csc-unit-test";
    protected static final String OPERATOR = "UTOP1";
    protected static final String WHATEVER = "whatever";

    protected OperateRequest operateRequest() {
        OperateRequest result = new OperateRequest();
        result.setClientId(CLIENT_ID);
        result.setOperator("unittest");
        return result;
    }

}
