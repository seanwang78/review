package com.uaepay.pub.csc.domainservice.compare.data.es;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.ParsingException;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.common.util.EsUtil;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;
import com.uaepay.pub.csc.domainservice.compare.data.impl.AbstractSrcDataIterator;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.data.es.EsSqlTemplateReplacer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * es默认源数据迭代器工厂
 * 
 * @author zc
 */
@Service
public class EsSrcDataIteratorFactory {

    @Autowired
    private CompareProperties compareProperties;

    public SrcDataIterator create(EsDataSourceConfig dataSourceConfig, String sqlTemplate, int splitMinutes,
        String relateField, QueryParam queryParam) {
        // 替换特殊占位符，否则解析会报错
        String tempTemplate = new EsSqlTemplateReplacer(sqlTemplate).replaceNumber(SqlTemplateParamEnum.BEGIN_TIME, 0)
            .replaceNumber(SqlTemplateParamEnum.END_TIME, 0).getSql();
        String indexName = EsUtil.extractIndexName(tempTemplate, "source sql");
        return new EsSrcDataIterator(sqlTemplate, splitMinutes, relateField, compareProperties.getQueryPageSize(),
            queryParam, indexName, dataSourceConfig.getRestHighLevelClient());
    }

    @Slf4j
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class EsSrcDataIterator extends AbstractSrcDataIterator {

        public EsSrcDataIterator(String sqlTemplate, int splitMinutes, String relateField, int pageSize,
            QueryParam queryParam, String indexName, RestHighLevelClient restHighLevelClient) {
            super(sqlTemplate, splitMinutes, relateField, pageSize, queryParam);
            this.indexName = indexName;
            this.restHighLevelClient = restHighLevelClient;
        }

        String indexName;
        RestHighLevelClient restHighLevelClient;

        @Override
        protected void init() {
            ParameterValidate.assertTrue("{begin} not exist", SqlTemplateParamEnum.BEGIN_TIME.find(sqlTemplate));
            ParameterValidate.assertTrue("{end} not exist", SqlTemplateParamEnum.END_TIME.find(sqlTemplate));
        }

        @Override
        protected List<QuerySplitParam> splitQueryParam() {
            return QueryParamSplitter.splitQueryParamByTime(queryParam, splitMinutes);
        }

        @Override
        protected SrcRows query(QuerySplitParam querySplitParam, int page, int pageSize) {
            SearchRequest query = buildSearchRequest(querySplitParam, page, pageSize);
            SearchHit[] batch = EsUtil.processSearch(restHighLevelClient, query);
            ColumnData columnData = ColumnData.fromEs(batch, relateField);
            return new SrcRows(RowDataConverter.fromEs(batch, columnData));
        }

        private SearchRequest buildSearchRequest(QuerySplitParam querySplitParam, int page, int pageSize) {
            // 准备参数
            String template = new EsSqlTemplateReplacer(sqlTemplate)
                .replaceDate(SqlTemplateParamEnum.BEGIN_TIME, querySplitParam.getBeginTime())
                .replaceDate(SqlTemplateParamEnum.END_TIME, querySplitParam.getEndTime()).getSql();
            JSONObject sqlObject = JSON.parseObject(template);
            sqlObject.remove(EsUtil.INDEX);
            JSONObject queryObject = (JSONObject)sqlObject.remove("query");
            int from = (page - 1) * pageSize;

            // 构造search请求
            SearchRequest request = new SearchRequest(indexName);
            try {
                XContentParser jsonParser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY,
                    LoggingDeprecationHandler.INSTANCE, sqlObject.toJSONString());
                SearchSourceBuilder sourceBuilder = SearchSourceBuilder.fromXContent(jsonParser);
                sourceBuilder.query(QueryBuilders.wrapperQuery(queryObject.toJSONString()));
                sourceBuilder.from(from);
                sourceBuilder.size(pageSize);
                request.source(sourceBuilder);
            } catch (IOException | ParsingException e) {
                throw new FailException(CommonReturnCode.CONFIG_ERROR, "build source query fail: " + e.getMessage());
            }
            return request;
        }

    }

}
