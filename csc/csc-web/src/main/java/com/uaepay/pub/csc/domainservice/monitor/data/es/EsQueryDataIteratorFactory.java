package com.uaepay.pub.csc.domainservice.monitor.data.es;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.parser.Feature;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.exception.fail.InvalidParameterException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.common.util.EsUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domain.enums.EsAggregationBuilderParserEnum;
import com.uaepay.pub.csc.domain.enums.EsPipelineAggregationBuilderParserEnum;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domain.properties.MonitorProperties;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.data.es.EsSqlTemplateReplacer;
import com.uaepay.pub.csc.domainservice.data.mongo.MongoSqlTemplateReplacer;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zc
 */
@Slf4j
@Service
@AllArgsConstructor
public class EsQueryDataIteratorFactory {
    private static final String AGGS = "aggs";
    private static final String AGGREGATIONS = "aggregations";
    private MonitorProperties monitorProperties;

    public QueryDataIterator create(EsDataSourceConfig dataSourceConfig, MonitorDefine define, QueryParam queryParam) {
        // 替换特殊占位符，否则解析会报错
        String tempTemplate =
            new MongoSqlTemplateReplacer(define.getQueryTemplate()).replaceNumber(SqlTemplateParamEnum.BEGIN_TIME, 0)
                .replaceNumber(SqlTemplateParamEnum.END_TIME, 0).replaceNumber(SqlTemplateParamEnum.COUNT, 0).getSql();
        String indexName = EsUtil.extractIndexName(tempTemplate, "query template");
        return new EsQueryDataIterator(queryParam, define, indexName, dataSourceConfig.getRestHighLevelClient());
    }

    @Data
    public class EsQueryDataIterator implements QueryDataIterator {
        public EsQueryDataIterator(QueryParam queryParam, MonitorDefine define, String indexName,
            RestHighLevelClient restHighLevelClient) {
            if (define.getSplitStrategy() == SplitStrategyEnum.NO) {
                this.splitParamsIterator = QueryParamSplitter.splitQueryParamByTime(queryParam, 0).iterator();
            } else {
                this.splitParamsIterator =
                    QueryParamSplitter.splitQueryParamByTime(queryParam, define.getSplitMinutes()).iterator();
            }

            this.queryTemplate = define.getQueryTemplate();
            this.monitorType = define.getMonitorType();
            this.keyField = define.getKeyField();
            this.splitStrategy = define.getSplitStrategy();
            this.indexName = indexName;
            this.restHighLevelClient = restHighLevelClient;
            validate();
        }

        Iterator<QuerySplitParam> splitParamsIterator;
        String queryTemplate;
        MonitorTypeEnum monitorType;
        String keyField;
        SplitStrategyEnum splitStrategy;
        String indexName;
        RestHighLevelClient restHighLevelClient;
        AggParseResult aggParseResult;

        protected void validate() {
            ParameterValidate.assertTrue("{begin} not exist", SqlTemplateParamEnum.BEGIN_TIME.find(queryTemplate));
            ParameterValidate.assertTrue("{end} not exist", SqlTemplateParamEnum.END_TIME.find(queryTemplate));
        }

        @Override
        public boolean hasNext() {
            return splitParamsIterator != null && splitParamsIterator.hasNext();
        }

        @Override
        public QueryRows next() {
            SearchRequest query = buildSearchRequest(splitParamsIterator.next());
            // 查询聚合和明细的逻辑分开处理
            if (aggParseResult == null) {
                SearchHit[] batch = EsUtil.processSearch(restHighLevelClient, query);
                ColumnData columnData = ColumnData.fromEs(batch, keyField);
                return new QueryRows(RowDataConverter.fromEs(batch, columnData));
            } else {
                Aggregations aggregations = EsUtil.processSearchAggregations(restHighLevelClient, query);
                ColumnData columnData = ColumnData.fromEs(aggParseResult, keyField);
                return new QueryRows(RowDataConverter.fromEs(aggregations, columnData));
            }
        }

        private SearchRequest buildSearchRequest(QuerySplitParam querySplitParam) {
            // 准备参数
            String template = new EsSqlTemplateReplacer(queryTemplate)
                .replaceDate(SqlTemplateParamEnum.BEGIN_TIME, querySplitParam.getBeginTime())
                .replaceDate(SqlTemplateParamEnum.END_TIME, querySplitParam.getEndTime()).getSql();

            JSONObject sqlObject = JSON.parseObject(template, Feature.OrderedField);
            sqlObject.remove(EsUtil.INDEX);
            JSONObject queryObject = (JSONObject)sqlObject.remove("query");

            JSONObject aggregations = (JSONObject)sqlObject.remove(AGGS);
            if (aggregations == null) {
                aggregations = (JSONObject)sqlObject.remove(AGGREGATIONS);
            }

            // 构造search请求
            SearchRequest request = new SearchRequest(indexName);
            try {
                XContentParser jsonParser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                    LoggingDeprecationHandler.INSTANCE, sqlObject.toJSONString());
                SearchSourceBuilder sourceBuilder = SearchSourceBuilder.fromXContent(jsonParser);
                sourceBuilder.query(QueryBuilders.wrapperQuery(queryObject.toJSONString()));

                if (aggregations != null) {
                    aggParseResult = parseAggs(aggregations);
                    if (!aggParseResult.isEmpty()) {
                        aggParseResult.getAggBuilders().forEach(sourceBuilder::aggregation);
                        aggParseResult.getPipelineAggBuilders().forEach(sourceBuilder::aggregation);
                    } else {
                        throw new FailException("BAD_SQL", "invalid aggregation params");
                    }
                    // 有聚合， 明细查询设置为0条
                    sourceBuilder.size(0);
                } else {
                    sourceBuilder.size(monitorProperties.getMaxDetailCount(monitorType) + 1);
                }

                request.source(sourceBuilder);
            } catch (IOException | ParsingException e) {
                throw new FailException(CommonReturnCode.CONFIG_ERROR, "build query fail: " + e.getMessage());
            }
            return request;
        }

        private AggParseResult parseAggs(JSONObject aggregationsJson) {
            Set<String> aggNameSet = aggregationsJson.keySet();
            AggParseResult result = new AggParseResult();
            JSONObject aggsJson = null;
            for (String aggName : aggNameSet) {
                JSONObject aggsContent = aggregationsJson.getJSONObject(aggName);
                Set<String> aggTypeSet = aggsContent.keySet();
                if (aggTypeSet.contains(AGGS)) {
                    aggsJson = (JSONObject)aggsContent.remove(AGGS);
                } else if (aggTypeSet.contains(AGGREGATIONS)) {
                    aggsJson = (JSONObject)aggsContent.remove(AGGREGATIONS);
                }
                ParameterValidate.assertTrue("语法错误", aggTypeSet.size() == 1);

                String aggType = aggTypeSet.iterator().next();
                JSONObject innerAggsContent = aggsContent.getJSONObject(aggType);
                if (!Constants.AGG_TYPE_TERMS.equals(aggType)) {
                    ParameterValidate.assertTrue("only terms can add sub-aggregations", aggsJson == null);
                } else {
                    ParameterValidate.assertTrue("terms 相同层级只能有一个", aggNameSet.size() == 1);
                }
                EsAggregationBuilderParserEnum parserEnum = EsAggregationBuilderParserEnum.getByAggType(aggType);
                if (parserEnum != null) {
                    result.addAggBuilder(parserEnum.parse(innerAggsContent, aggName));
                    continue;
                }
                EsPipelineAggregationBuilderParserEnum pipelineEnum =
                    EsPipelineAggregationBuilderParserEnum.getByAggType(aggType);
                if (pipelineEnum != null) {
                    result.addPipelineAggBuilder(pipelineEnum.parse(innerAggsContent, aggName));
                    continue;
                }
                log.info("aggs info: {}, {}", aggName, innerAggsContent);
                throw new InvalidParameterException("不支持的聚合类型: " + aggType);
            }

            if (aggsJson != null) {
                AggParseResult subResult = parseAggs(aggsJson);
                if (!subResult.isEmpty()) {
                    if (result.getAggBuilders().size() == 1) {
                        subResult.getAggBuilders().forEach(result.getAggBuilders().get(0)::subAggregation);
                        subResult.getPipelineAggBuilders().forEach(result.getAggBuilders().get(0)::subAggregation);
                    }
                }
            }
            return result;
        }

        @Getter
        @Setter
        public class AggParseResult {
            List<AggregationBuilder> aggBuilders = new ArrayList<>();
            List<PipelineAggregationBuilder> pipelineAggBuilders = new ArrayList<>();

            public void addAggBuilder(AggregationBuilder aggBuilder) {
                aggBuilders.add(aggBuilder);
            }

            public void addPipelineAggBuilder(PipelineAggregationBuilder pipelineAggBuilder) {
                pipelineAggBuilders.add(pipelineAggBuilder);
            }

            public boolean isEmpty() {
                return aggBuilders.isEmpty() && pipelineAggBuilders.isEmpty();
            }
        }

    }
}
