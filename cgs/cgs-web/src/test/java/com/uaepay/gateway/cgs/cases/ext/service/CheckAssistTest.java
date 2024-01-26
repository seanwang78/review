package com.uaepay.gateway.cgs.cases.ext.service;

import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.facade.domain.Header;
import com.uaepay.gateway.cgs.app.facade.enums.ApiType;
import com.uaepay.gateway.cgs.app.facade.enums.PlatformType;
import com.uaepay.gateway.cgs.constants.HttpHeaderKey;
import com.uaepay.gateway.cgs.domain.ReceiveOrderContext;
import com.uaepay.gateway.cgs.domain.api.ApiConfig;
import com.uaepay.gateway.cgs.ext.service.assist.CheckAssist;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;
import com.uaepay.gateway.common.facade.enums.LangType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查辅助类 .
 * <p>
 *
 * @author yusai
 * @date 2020-07-15 21:54.
 */
@Slf4j
public class CheckAssistTest extends ApplicationTestBase {

    @Autowired
    CheckAssist checkAssist;

    @Test
    public void whitelistCheck() {

        ReceiveOrderContext context = new ReceiveOrderContext(null,null,false, null);
        context.setLang(LangType.ARAB);
        AccessMember accessMember = new AccessMember();

        // accessMember.setMemberId("1000);
        accessMember.setPartnerId("1000000578012");

        context.setAccessMember(accessMember);

        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setApiType(ApiType.AUTHED);
        context.setApiConfig(apiConfig);
        checkAssist.whitelistCheck(context);

        log.info("result : {}",context);
    }


    @Test
    public void checkGuard () {

        ReceiveOrderContext context = new ReceiveOrderContext(null,null,false, null);
        context.setLang(LangType.ARAB);
        AccessMember accessMember = new AccessMember();

        accessMember.setIdentity("2019112200548548");
        accessMember.setMemberId("100000054015");
        accessMember.setPartnerId("200000000007");

        context.setAccessMember(accessMember);
        Header h = new Header();

        Map<String,String> hmaps = new HashMap<>();
        hmaps.put(HttpHeaderKey.X_GUARDTOKEN,"aaa");
        hmaps.put(HttpHeaderKey.X_HOSTAPP,"totok-pay");
        h.setHeaderMaps(hmaps);
        context.setHeader(h);

        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setApiType(ApiType.AUTHED);
        context.setApiConfig(apiConfig);
        context.setPlatformType(PlatformType.Android);

        checkAssist.checkGuard(context);

        log.info("--->{}",context);

    }}
