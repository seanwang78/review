package com.uaepay.pub.csc.domainservice.data;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.QuerySplitParam;

/**
 * @author zc
 */
public class QueryParamSplitter {

    /**
     * 切分查询时间参数
     *
     * @param queryParam
     *            时间范围
     * @param splitMinutes
     *            切分时长
     * @return 切分结果
     */
    public static List<QuerySplitParam> splitQueryParamByTime(QueryParam queryParam, int splitMinutes) {
        List<QuerySplitParam> result = new ArrayList<>();
        DateTime beginTime = new DateTime(queryParam.getBeginTime());
        DateTime endTime = new DateTime(queryParam.getEndTime());
        if (beginTime.compareTo(endTime) >= 0) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, "BeginTime must be before endTime!");
        }
        if (splitMinutes <= 0) {
            result.add(QuerySplitParam.builder().beginTime(queryParam.getBeginTime()).endTime(queryParam.getEndTime())
                .build());
            return result;
        }
        DateTime splitBeginTime = new DateTime(beginTime);
        while (splitBeginTime.compareTo(endTime) < 0) {
            DateTime splitEndTime = splitBeginTime.plusMinutes(splitMinutes);
            if (splitEndTime.compareTo(endTime) > 0) {
                splitEndTime = endTime;
            }
            result.add(
                QuerySplitParam.builder().beginTime(splitBeginTime.toDate()).endTime(splitEndTime.toDate()).build());
            splitBeginTime = splitEndTime;
        }
        return result;
    }

}
