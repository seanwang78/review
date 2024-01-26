package com.uaepay.gateway.cgs.test.base;

import com.uaepay.gateway.cgs.CgsTestApplication;
import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;
import com.uaepay.gateway.cgs.app.service.domain.AccessInfo;
import com.uaepay.gateway.cgs.app.service.domain.LoginToken;
import com.uaepay.gateway.cgs.app.service.domainservice.token.AccessTokenService;
import com.uaepay.gateway.cgs.test.common.RequestBuilder;
import com.uaepay.gateway.cgs.test.common.ResponseChecker;
import com.uaepay.gateway.cgs.test.mock.domain.MockAccess;
import com.uaepay.gateway.cgs.test.mock.repository.AccessMocker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CgsTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public abstract class ApplicationTestBase {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String CLIENT_ID = "cgs_unit_test";

    protected static final String UAT_PUBLIC_KEY_1 =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArQBG4bYMrohTXe4noNpqDzBTPY+RVr2jDkya2InlYarmHwQiD12qypw+Lh0KYXbD8kctAkewPbU9R08SdpCWq7Kh3Gt/YfgJxgjMANzwVe7R57Kb4ruFBAdbmbx3baO7Pym2fyiRHQlqU6JnNHKB18IA3iN1AjWCsv8AcuSVRbt+j1nsHULcdSAUzfcc7TPRUGDXj6TWa5GhHopvuzMxGF4qSjMuiQe1GrHm0IrW+XCkgOI5g0Gi8PCrRdQwAzu386lN833EyZQ94ciNW2giB4YLVVzzuJOJPt32R+Ns1E0S5P/yBAEqCw/NJP0EgHrguc0PYTFg7BS7gveZI5oONQIDAQAB";

    protected static final String UAT_PUBLIC_KEY_2 =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyf8lZPJmOcpWOsB+qh0hByH4W7+cmZDErMlyK6DRAIOYWE/BFXXhPY+Q2M9OtdOXkvTxxp0aH8hC9OR5S0KCgrDOh9a1gOyOLJECQlIJh4amc5Od/6yEPbSDNui4JtVQHYhfxD7IWHzLBSR80Z3XEHr87RDvQY1yeS0wYre9TCSUDLUMlGRK4LLigzSHdMsVxeWIlwghzj4tM7pRppkQ6Xygs9V0GunLRNwPneCSZ2K2mhWyy3OohVG+Sp8+BrYHMNI6YE2N9SLu0e/ukAfNKFRTJMchc4t7GqI1BBXrHXvoiGsYNWDL5gD+v5diww/NJocUG65Uql++nDqG0mNnIQIDAQAB";

    @LocalServerPort
    protected int port;

    protected String url;

    @Before
    public void init() {
        url = "http://localhost:" + port + "/cgs/api/";
        logger.info("url: {}", url);
    }

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    protected AccessTokenService accessTokenService;

    @Autowired
    protected AccessMocker accessMocker;

    protected MockAccess mockAccess() {
        return accessMocker.mock(new MockAccess());
    }

    protected AccessInfo mockAccess(String identity, String platform, String memberId) {
        LoginToken.KeyInfo keyInfo = new LoginToken.KeyInfo(identity, platform, null);
        AccessMember accessMember = new AccessMember();
        accessMember.setMemberId(memberId);
        LoginToken loginToken = new LoginToken(keyInfo, accessMember);
        return accessTokenService.login(loginToken);
    }

    protected String mockSalt(String accessToken) {
        return accessTokenService.generateSalt(accessToken);
    }

    protected ResponseChecker processRequest(RequestBuilder builder) {
        return processRequest(builder, null);
    }

    protected ResponseChecker processRequest(RequestBuilder builder, TestRestTemplate restTemplate) {
        if (restTemplate == null) {
            restTemplate = testRestTemplate;
        }
        ResponseEntity<String> response =
            restTemplate.postForEntity(url + builder.getService(), builder.build(), String.class);
        Assert.assertEquals(200, response.getStatusCodeValue());
        return new ResponseChecker(response.getBody());
    }

}
