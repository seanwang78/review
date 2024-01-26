package com.uaepay.pub.csc.domain.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zc
 */
public enum EsPipelineAggregationBuilderParserEnum {

    /**  */
    BUCKET_SELECTOR("bucket_selector", (aggContent, aggName) -> {
        JSONObject pathParams = aggContent.getJSONObject("buckets_path");
        Map<String, String> bucketsPathsMap = new HashMap<>();
        if (pathParams != null) {
            for (String key : pathParams.keySet()) {
                bucketsPathsMap.put(key, pathParams.getString(key));
            }
        }
        String scriptString = aggContent.getString("script");
        Script script = new Script(scriptString);
        return PipelineAggregatorBuilders.bucketSelector(aggName, bucketsPathsMap, script);
    }),

    BUCKET_SCRIPT("bucket_script", (aggContent, aggName) -> {
        JSONObject pathParams = aggContent.getJSONObject("buckets_path");
        Map<String, String> bucketsPathsMap = new HashMap<>();
        if (pathParams != null) {
            for (String key : pathParams.keySet()) {
                bucketsPathsMap.put(key, pathParams.getString(key));
            }
        }
        String scriptString = aggContent.getString("script");
        Script script = new Script(scriptString);
        String format = aggContent.getString("format");
        return PipelineAggregatorBuilders.bucketScript(aggName, bucketsPathsMap, script).format(format);
    }),

    BUCKET_SORT("bucket_sort", (aggContent, aggName) -> {
        JSONArray sortArray = aggContent.getJSONArray("sort");
        List<FieldSortBuilder> sorts = new ArrayList<>();
        if (sortArray != null) {
            for (Object sort : sortArray) {
                JSONObject sortJson = (JSONObject)sort;
                String fieldName = sortJson.keySet().iterator().next();
                JSONObject orderJson = (JSONObject)sortJson.values().iterator().next();
                sorts.add(new FieldSortBuilder(fieldName).order(SortOrder.fromString(orderJson.getString("order"))));
            }
        }
        Integer size = aggContent.getInteger("size");
        return PipelineAggregatorBuilders.bucketSort(aggName, sorts).size(size);
    }),

    ;

    EsPipelineAggregationBuilderParserEnum(String aggregationType,
        EsPipelineAggregationBuilderParser esPipelineAggregationBuilderParser) {
        this.aggregationType = aggregationType;
        this.esPipelineAggregationBuilderParser = esPipelineAggregationBuilderParser;
    }

    public static EsPipelineAggregationBuilderParserEnum getByAggType(String aggregationType) {
        for (EsPipelineAggregationBuilderParserEnum parserEnum : EsPipelineAggregationBuilderParserEnum.values()) {
            if (parserEnum.aggregationType.equals(aggregationType)) {
                return parserEnum;
            }
        }
        return null;
    }

    public PipelineAggregationBuilder parse(JSONObject aggContent, String aggName) {
        return esPipelineAggregationBuilderParser.parse(aggContent, aggName);
    }

    private final String aggregationType;

    private final EsPipelineAggregationBuilderParser esPipelineAggregationBuilderParser;

    interface EsPipelineAggregationBuilderParser {
        PipelineAggregationBuilder parse(JSONObject aggContent, String aggName);
    }

}
