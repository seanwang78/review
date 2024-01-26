package com.uaepay.pub.csc.cases.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.pub.csc.core.common.util.FormatUtil;

public class FormatUtilTest {

    @Test
    public void testUpper() {
        Assertions.assertNull(FormatUtil.upper(null));
        Assertions.assertEquals("SIM", FormatUtil.upper("sim"));
    }

    @Test
    public void testDateTime() {
        Assertions.assertNull(FormatUtil.datetime(null));

        Date date = new DateTime(2020, 3, 1, 11, 45, 0).toDate();
        Assertions.assertEquals("2020-03-01 11:45:00", FormatUtil.datetime(date));
    }

    @Test
    public void testDuration() {
        Assertions.assertNull(FormatUtil.datetime(null));

        Date begin = new DateTime(2020, 3, 1, 11, 30, 0).toDate();
        Date end = new DateTime(2020, 3, 1, 11, 45, 0).toDate();
        Assertions.assertEquals("PT15M", FormatUtil.duration(begin, end));
        Assertions.assertEquals("PT-15M", FormatUtil.duration(end, begin));
    }

}
