package com.uaepay.pub.csc.core.common.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.domain.data.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 */
@Slf4j
public class EsUtil {

    public static final String INDEX = "index";

    public static String extractIndexName(String sqlTemplate, String templateName) {
        String databaseName;
        try {
            JSONObject commend = JSON.parseObject(sqlTemplate);
            databaseName = (String)commend.get(INDEX);
        } catch (Throwable e) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, templateName + " error");
        }
        if (StringUtils.isBlank(databaseName)) {
            throw new FailException(CommonReturnCode.CONFIG_ERROR, templateName + " 'index' not exist");
        }
        return databaseName;
    }

    public static SearchHit[] processSearch(RestHighLevelClient restHighLevelClient, SearchRequest request) {
        SearchResponse result;
        try {
            result = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ErrorException("execute search error", e);
        }
        if (result.status() != RestStatus.OK) {
            throw new ErrorException("execute error" + result);
        }
        return result.getHits().getHits();
    }

    public static Aggregations processSearchAggregations(RestHighLevelClient restHighLevelClient,
        SearchRequest request) {
        SearchResponse result;
        try {
            // 打印请求报文
            log.info("es source: {}", request.source());
            result = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ErrorException("execute search error", e);
        }
        if (result.status() != RestStatus.OK) {
            throw new ErrorException("execute error" + result);
        }

        // 没有查到数据
        if (result.getHits().getTotalHits().value == 0) {
            return null;
        } else {
            return result.getAggregations();
        }
    }

    public static void parseAggregations(Aggregations aggregations, Map<String, Object> row,
        List<Map<String, Object>> rowList) {
        Map<String, Aggregation> asMap = aggregations.getAsMap();
        Set<String> aggNames = asMap.keySet();
        boolean leaf = false;
        for (String aggName : aggNames) {
            Aggregation aggregation = asMap.get(aggName);
            if (!(aggregation instanceof ParsedTerms)) {
                leaf = true;
            }
            if (aggregation instanceof ParsedTerms) {
                List<? extends Terms.Bucket> parsedTermsList = ((ParsedTerms)aggregation).getBuckets();
                for (int i = 0; i < parsedTermsList.size(); i++) {
                    Map<String, Object> newRow = new HashMap();
                    newRow.putAll(row);
                    Terms.Bucket item = parsedTermsList.get(i);
                    newRow.put(aggName, item.getKeyAsString() == null ? item.getKey() : item.getKeyAsString());
                    newRow.put(Constants.COLUMN_COUNT, item.getDocCount());
                    if (item.getAggregations().asList().size() > 0) {
                        parseAggregations(item.getAggregations(), newRow, rowList);
                    } else {
                        rowList.add(newRow);
                    }
                }
            } else if (aggregation instanceof ParsedAvg) {
                try {
                    row.put(aggName, new BigDecimal(((ParsedAvg)aggregation).getValueAsString()));
                } catch (Exception e) {
                    row.put(aggName, ((ParsedAvg)aggregation).getValueAsString());
                }
            } else if (aggregation instanceof ParsedSum) {
                try {
                    row.put(aggName, new BigDecimal(((ParsedSum)aggregation).getValueAsString()));
                } catch (Exception e) {
                    row.put(aggName, ((ParsedSum)aggregation).getValueAsString());
                }
            } else if (aggregation instanceof ParsedMin) {
                try {
                    row.put(aggName, new BigDecimal(((ParsedMin)aggregation).getValueAsString()));
                } catch (Exception e) {
                    row.put(aggName, ((ParsedMin)aggregation).getValueAsString());
                }
            } else if (aggregation instanceof ParsedMax) {
                try {
                    row.put(aggName, new BigDecimal(((ParsedMax)aggregation).getValueAsString()));
                } catch (Exception e) {
                    row.put(aggName, ((ParsedMax)aggregation).getValueAsString());
                }
            } else if (aggregation instanceof ParsedValueCount) {
                row.put(aggName, ((ParsedValueCount)aggregation).getValue());
            } else if (aggregation instanceof ParsedTopHits) {
                ParsedTopHits topHits = (ParsedTopHits)aggregation;
                SearchHit[] result = topHits.getHits().getHits();
                if (result != null && result.length > 0) {
                    SearchHit hit = result[0];
                    Map<String, Object> source = hit.getSourceAsMap();
                    row.putAll(source);
                }
            }
        }
        if (leaf) {
            rowList.add(row);
        }
    }

}
