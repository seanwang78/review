package com.uaepay.gateway.cgs.app.template.testtool.common;

/**
 * 默认响应校验器
 * 
 * @author zc
 */
public class ResponseChecker extends AbstractResponseChecker<ResponseChecker> {

    public ResponseChecker(String response) {
        super(response);
    }

    // public ResponseChecker tokenRefreshed() {
    // Assert.assertTrue("基本参数非空: access_token",
    // StringUtils.isNotBlank(bodyJson.getString("access_token")));
    // Assert.assertTrue("基本参数非空: access_key",
    // StringUtils.isNotBlank(bodyJson.getString("access_key")));
    // return this;
    // }

}
