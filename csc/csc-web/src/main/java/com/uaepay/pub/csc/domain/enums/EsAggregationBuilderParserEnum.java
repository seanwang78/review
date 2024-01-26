package com.uaepay.pub.csc.domain.enums;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;

import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.util.ParameterValidate;

/**
 * @author caoyongxing 请求聚合解析枚举 有新增枚举时，需要同步修改 结果解析方法
 *         com.uaepay.pub.csc.domain.data.RowDataConverter#parseAggregations(org.elasticsearch.search.aggregations.Aggregations,
 *         java.util.Map, java.util.List, com.uaepay.pub.csc.domain.data.ColumnData)
 */
public enum EsAggregationBuilderParserEnum {
    /** terms */
    TERMS("terms", (aggContent, aggName) -> {
        String field = aggContent.getString("field");
        Integer size = aggContent.getInteger("size");
        ParameterValidate.assertTrue("terms聚合必须设置size", size != null);
        String format = aggContent.getString("format");
        TermsAggregationBuilder result = AggregationBuilders.terms(aggName).field(field).size(size);
        if (format != null) {
            result.format(format);
        }
        return result;
    }),

    /** min */
    MIN("min", (aggContent, aggName) -> {
        String field = aggContent.getString("field");
        String format = aggContent.getString("format");
        MinAggregationBuilder result = AggregationBuilders.min(aggName).field(field);
        if (format != null) {
            result.format(format);
        }
        return result;
    }),

    /** max */
    MAX("max", (aggContent, aggName) -> {
        String field = aggContent.getString("field");
        String format = aggContent.getString("format");
        MaxAggregationBuilder result = AggregationBuilders.max(aggName).field(field);
        if (format != null) {
            result.format(format);
        }
        return result;
    }),

    /** avg */
    AVG("avg", (aggContent, aggName) -> {
        String field = aggContent.getString("field");
        String format = aggContent.getString("format");
        AvgAggregationBuilder result = AggregationBuilders.avg(aggName).field(field);
        if (format != null) {
            result.format(format);
        }
        return result;
    }),

    /** sum */
    SUM("sum", (aggContent, aggName) -> {
        String field = aggContent.getString("field");
        String format = aggContent.getString("format");
        SumAggregationBuilder result = AggregationBuilders.sum(aggName).field(field);
        if (format != null) {
            result.format(format);
        }
        return result;
    }),

    /** count */
    VALUE_COUNT("value_count", (aggContent, aggName) -> {
        String field = aggContent.getString("field");
        String format = aggContent.getString("format");
        ValueCountAggregationBuilder result = AggregationBuilders.count(aggName).field(field);
        if (format != null) {
            result.format(format);
        }
        return result;
    }),

    FILTER("filter", ((aggContent, aggName) -> {
        WrapperQueryBuilder query = QueryBuilders.wrapperQuery(aggContent.toJSONString());
        return AggregationBuilders.filter(aggName, query);
    })),

    ;

    EsAggregationBuilderParserEnum(String aggregationType, EsAggregationBuilderParser esAggregationBuilderParser) {
        this.aggregationType = aggregationType;
        this.esAggregationBuilderParser = esAggregationBuilderParser;
    }

    public static EsAggregationBuilderParserEnum getByAggType(String aggregationType) {
        for (EsAggregationBuilderParserEnum parserEnum : EsAggregationBuilderParserEnum.values()) {
            if (parserEnum.aggregationType.equals(aggregationType)) {
                return parserEnum;
            }
        }
        return null;
    }

    public AggregationBuilder parse(JSONObject aggContent, String aggName) {
        return esAggregationBuilderParser.parse(aggContent, aggName);
    }

    private final String aggregationType;

    private final EsAggregationBuilderParser esAggregationBuilderParser;

    interface EsAggregationBuilderParser {
        AggregationBuilder parse(JSONObject aggContent, String aggName);
    }

}
