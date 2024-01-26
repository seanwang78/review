package com.uaepay.pub.csc.domainservice.logmonitor.repository.impl;

import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.common.util.EsUtil;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.domain.data.Constants;
import com.uaepay.pub.csc.domain.data.DataSourceConfig;
import com.uaepay.pub.csc.domain.data.EsDataSourceConfig;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.domainservice.data.event.DataSourceRegisteredEvent;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.CgsLogRepository;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cyx
 */
@Slf4j
@Repository
public class CgsLogRepositoryImpl implements CgsLogRepository, ApplicationListener<DataSourceRegisteredEvent> {

    public static final String APP_CODE = "appCode";
    public static final String API_CODE = "apiCode";
    public static final String RETURN_CODE = "returnCode";
    public static final String RETURN_MSG = "returnMsg";
    public static final String TOP_TID = "topTid";
    public static final String REQUEST_TIME = "requestTime";
    public static final String TID = "tid";
    public static final String KEYWORD = ".keyword";
    public static final String SUCCESS_CODE = "200";
    public static final int DEFAULT_BUCKET_SIZE = 100;
    public static final int CODE_BUCKET_SIZE = 20;
    public static final int MESSAGE_BUCKET_SIZE = 10;
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyyMMdd");

    @Value("${log_monitor.cgsPorterIndexPrefix:porter_cgs-}")
    public String indexName;

    RestHighLevelClient client;

    @Override
    public List<LogStat> statCgsLog(Date beginTime, Date endTime) {
        SearchRequest request = new SearchRequest(indexName + DATE_FORMAT.format(beginTime));
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.rangeQuery(REQUEST_TIME).from(beginTime).to(endTime).includeUpper(false));
        boolQuery.mustNot(QueryBuilders.termQuery(RETURN_CODE + KEYWORD, SUCCESS_CODE));
        sourceBuilder.query(boolQuery);
        sourceBuilder.size(0);

        AggregationBuilder appCodeBuilder = AggregationBuilders.terms(APP_CODE).field(APP_CODE + KEYWORD)
            .size(DEFAULT_BUCKET_SIZE).order(BucketOrder.key(true));
        AggregationBuilder apiCodeBuilder = AggregationBuilders.terms(API_CODE).field(API_CODE + KEYWORD)
            .size(DEFAULT_BUCKET_SIZE).order(BucketOrder.key(true));
        AggregationBuilder returnCodeBuilder = AggregationBuilders.terms(RETURN_CODE).field(RETURN_CODE + KEYWORD)
            .size(CODE_BUCKET_SIZE).order(BucketOrder.key(true));
        AggregationBuilder returnMsgBuilder = AggregationBuilders.terms(RETURN_MSG).field(RETURN_MSG + KEYWORD)
            .size(MESSAGE_BUCKET_SIZE).order(BucketOrder.key(true));
        AggregationBuilder topHintBuilder = AggregationBuilders.topHits(TOP_TID).fetchSource(TID, null)
            .sort(SortBuilders.fieldSort(REQUEST_TIME).order(SortOrder.DESC)).size(1);

        appCodeBuilder.subAggregation(apiCodeBuilder);
        apiCodeBuilder.subAggregation(returnCodeBuilder);
        returnCodeBuilder.subAggregation(returnMsgBuilder);
        returnMsgBuilder.subAggregation(topHintBuilder);

        sourceBuilder.aggregation(appCodeBuilder);
        request.source(sourceBuilder);
        SearchResponse searchResponse;
        try {
            searchResponse = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("ES请求异常：{}", sourceBuilder);
            throw new RuntimeException("请求ES报错", e);
        }

        if (searchResponse.status() != RestStatus.OK) {
            log.error("ES请求异常：{}", sourceBuilder);
            throw new RuntimeException("调用es报错");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        EsUtil.parseAggregations(searchResponse.getAggregations(), new HashMap<>(), result);
        return convert(beginTime, endTime, result);
    }

    private List<LogStat> convert(Date beginTime, Date endTime, List<Map<String, Object>> list) {
        List<LogStat> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (Map<String, Object> item : list) {
            LogStat logStat = new LogStat();
            logStat.setApiCode((String)item.get(API_CODE));
            logStat.setAppCode((String)item.get(APP_CODE));
            logStat.setReturnCode((String)item.get(RETURN_CODE));
            logStat.setReturnMsg((String)item.get(RETURN_MSG));
            logStat.setLatestTid((String)item.get(TID));
            logStat.setValueCount((Long)item.get(Constants.COLUMN_COUNT));
            logStat.setStatus(LogStatStatusEnum.INIT.getCode());
            logStat.setBeginTime(beginTime);
            logStat.setEndTime(endTime);
            logStat.setCreateTime(new Date());
            logStat.setUpdateTime(new Date());
            result.add(logStat);
        }
        return result;
    }

    @Override
    public void onApplicationEvent(DataSourceRegisteredEvent dataSourceRegisteredEvent) {
        DataSourceConfigFactory dataSourceConfigFactory = dataSourceRegisteredEvent.getDataSourceConfigFactory();
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate("es_log_reader");
        if (dataSourceConfig instanceof EsDataSourceConfig) {
            client = ((EsDataSourceConfig)dataSourceConfig).getRestHighLevelClient();
        }
    }
}
