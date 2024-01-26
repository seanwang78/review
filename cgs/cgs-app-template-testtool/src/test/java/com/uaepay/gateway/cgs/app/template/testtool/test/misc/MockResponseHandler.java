package com.uaepay.gateway.cgs.app.template.testtool.test.misc;

import com.uaepay.gateway.cgs.app.template.misc.ResponseHandler;

/**
 * @author 刘智斌
 * @version 0.1
 * @time 2020/3/4
 * @since 0.1
 */
public class MockResponseHandler extends ResponseHandler {
    @Override
    public String resolve(String langType, String message, Object... params) {
        return "replace";
    }
}
