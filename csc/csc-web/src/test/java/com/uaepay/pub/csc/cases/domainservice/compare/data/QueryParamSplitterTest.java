package com.uaepay.pub.csc.cases.domainservice.compare.data;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.QuerySplitParam;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;

public class QueryParamSplitterTest {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testNormal_minutes() throws ParseException {
        List<QuerySplitParam> params = splitQueryParamByTime("2019-01-01 00:00:00", "2019-01-01 00:15:00", 10);
        System.out.println(params);
        Assertions.assertEquals(2, params.size());
        checkSrcQuerySplitParam(params.get(0), "2019-01-01 00:00:00", "2019-01-01 00:10:00", null);
        checkSrcQuerySplitParam(params.get(1), "2019-01-01 00:10:00", "2019-01-01 00:15:00", null);
    }

    @Test
    public void testNormal_hours() throws ParseException {
        List<QuerySplitParam> params = splitQueryParamByTime("2019-01-01 08:00:00", "2019-01-03 00:15:00", 1440);
        System.out.println(params);
        Assertions.assertEquals(2, params.size());
        checkSrcQuerySplitParam(params.get(0), "2019-01-01 08:00:00", "2019-01-02 08:00:00", null);
        checkSrcQuerySplitParam(params.get(1), "2019-01-02 08:00:00", "2019-01-03 00:15:00", null);
    }

    @Test
    public void testSplitMinuteZero() throws ParseException {
        List<QuerySplitParam> params = splitQueryParamByTime("2019-01-01 00:00:00", "2019-01-01 00:15:00", 0);
        System.out.println(params);
        Assertions.assertEquals(1, params.size());
        checkSrcQuerySplitParam(params.get(0), "2019-01-01 00:00:00", "2019-01-01 00:15:00", null);
    }

    @Test
    public void testBeginLaterThanEnd() {
        FailException exception = Assertions.assertThrows(FailException.class,
            () -> splitQueryParamByTime("2019-01-02 00:00:00", "2019-01-01 00:15:00", 10));
        Assertions.assertEquals("BeginTime must be before endTime!", exception.getMessage());
    }

    public List<QuerySplitParam> splitQueryParamByTime(String beginTime, String endTime, int splitMinutes)
        throws ParseException {
        QueryParam queryParam = new QueryParam();
        queryParam.setBeginTime(DATE_FORMAT.parse(beginTime));
        queryParam.setEndTime(DATE_FORMAT.parse(endTime));
        return QueryParamSplitter.splitQueryParamByTime(queryParam, splitMinutes);
    }

    public void checkSrcQuerySplitParam(QuerySplitParam querySplitParam, String beginTime, String endTime,
        String shardingSuffix) {
        Assertions.assertEquals(beginTime, DATE_FORMAT.format(querySplitParam.getBeginTime()));
        Assertions.assertEquals(endTime, DATE_FORMAT.format(querySplitParam.getEndTime()));
        Assertions.assertEquals(shardingSuffix, querySplitParam.getShardingSuffix());
    }

}
