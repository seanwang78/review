package com.uaepay.pub.csc.manual;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.ext.integration.MnsClient;
import com.uaepay.pub.csc.test.base.ManualTestBase;

@Disabled
public class MnsClientTest extends ManualTestBase {

    @Autowired
    MnsClient mnsClient;

    /**
     * CSC20200311155350336
     */
    @Test
    public void testEmail() {
        List<String> emails = Arrays.asList("cong.zhou@topay.ae", "shunxiang.lin@topay.ae");
        String subject = "test subject";
        String content = "test content csc";
        mnsClient.sendMail(emails, subject, content, null);
    }

    /**
     * CSC20200311155350336
     */
    @Test
    public void testZhibinEmail() {
        List<String> emails = Collections.singletonList("zhibin.liu@payby.com");
        String subject = "test subject";
        String content = "test content csc";
        mnsClient.sendMail(emails, subject, content, null);
    }

    /**
     * CSC20200311155445034
     */
    @Test
    public void testMobile() {
        List<String> emails = Arrays.asList("+971-585680720", "+971-585680720");
        String content = "test content csc";
        mnsClient.sendSMS(emails, content);
    }

}
