package com.uaepay.gateway.cgs.manual;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.gateway.cgs.app.facade.api.ClientGatewayFacade;
import com.uaepay.gateway.cgs.integration.app.AppServiceFactory;
import com.uaepay.gateway.cgs.test.base.ApplicationTestBase;

@Ignore
public class AppServiceManual extends ApplicationTestBase {

    @Autowired
    AppServiceFactory gatewayServiceFactory;

    @Test
    public void test() throws InterruptedException {
        ClientGatewayFacade service = gatewayServiceFactory.getOrCreate("cgs-app");
        for (int i = 0; i != 100; i++) {
            try {
                System.out.println(service.doService(null));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            Thread.sleep(3000);
        }
    }

}
