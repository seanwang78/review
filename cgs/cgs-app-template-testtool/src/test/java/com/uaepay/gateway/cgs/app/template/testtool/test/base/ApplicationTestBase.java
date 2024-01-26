package com.uaepay.gateway.cgs.app.template.testtool.test.base;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.uaepay.gateway.cgs.app.facade.api.ClientGatewayFacade;
import com.uaepay.gateway.cgs.app.template.testtool.common.RequestBuilder;
import com.uaepay.gateway.cgs.app.template.testtool.common.ResponseChecker;
import com.uaepay.gateway.cgs.app.template.testtool.test.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApplicationTestBase {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ClientGatewayFacade clientGatewayFacade;

    protected ResponseChecker doService(RequestBuilder requestBuilder) {
        String response = clientGatewayFacade.doService(requestBuilder.build());
        return new ResponseChecker(response);
    }

}
