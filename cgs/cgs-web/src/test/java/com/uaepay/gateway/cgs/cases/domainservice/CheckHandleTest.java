package com.uaepay.gateway.cgs.cases.domainservice;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.gateway.cgs.domainservice.check.CheckContext;
import com.uaepay.gateway.cgs.domainservice.check.CheckHandle;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * <p>
 *
 * @author yusai
 * @date 2020-02-27 15:12.
 */
public class CheckHandleTest extends ApplicationTestBase {

    @Autowired
    private CheckHandle checkHandle;

    @Test
    public void checkFildId() {
        CheckContext checkContext = new CheckContext(JSONObject.parseObject("{\"fileId\":\"dc3819d1d23b40c9bb859358050b7360\"}"),
                "fileId:UFS,filed:UFS");
        checkContext.setMemberId("100000012641");
        checkHandle.check(checkContext);
    }

}
