package com.uaepay.pub.csc.domainservice.monitor.data.mongo;

import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.core.common.util.MongoUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.MongoDataSourceConfig;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.QuerySplitParam;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domain.properties.MonitorProperties;
import com.uaepay.pub.csc.domainservice.data.MongoTemplateFactory;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.data.mongo.MongoSqlTemplateReplacer;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;

import lombok.Data;

/**
 * @author zc
 */
@Service
public class MongoQueryDataIteratorFactory {

    public MongoQueryDataIteratorFactory(MongoTemplateFactory mongoTemplateFactory,
        MonitorProperties monitorProperties) {
        this.mongoTemplateFactory = mongoTemplateFactory;
        this.monitorProperties = monitorProperties;
    }

    private MongoTemplateFactory mongoTemplateFactory;

    private MonitorProperties monitorProperties;

    public QueryDataIterator create(MongoDataSourceConfig dataSourceConfig, MonitorDefine define,
        QueryParam queryParam) {
        // 替换特殊占位符，否则解析会报错
        String tempTemplate =
            new MongoSqlTemplateReplacer(define.getQueryTemplate()).replaceNumber(SqlTemplateParamEnum.BEGIN_TIME, 0)
                .replaceNumber(SqlTemplateParamEnum.END_TIME, 0).replaceNumber(SqlTemplateParamEnum.COUNT, 0).getSql();
        String databaseName = MongoUtil.extractDatabaseName(tempTemplate, "query template");
        MongoTemplate mongoTemplate = mongoTemplateFactory.getOrCreate(dataSourceConfig, databaseName);
        return new MongoQueryDataIterator(mongoTemplate, queryParam, define);
    }

    @Data
    public class MongoQueryDataIterator implements QueryDataIterator {

        public MongoQueryDataIterator(MongoTemplate mongoTemplate, QueryParam queryParam, MonitorDefine define) {
            this.mongoTemplate = mongoTemplate;
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
            validate();
        }

        MongoTemplate mongoTemplate;
        Iterator<QuerySplitParam> splitParamsIterator;
        String queryTemplate;
        MonitorTypeEnum monitorType;
        String keyField;
        SplitStrategyEnum splitStrategy;

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
            Document query = buildQuery(splitParamsIterator.next());
            List<RowData> rows = MongoUtil.execute(mongoTemplate, query, keyField);
            return new QueryRows(rows);
        }

        private Document buildQuery(QuerySplitParam querySplitParam) {
            String template = new MongoSqlTemplateReplacer(queryTemplate)
                .replaceDate(SqlTemplateParamEnum.BEGIN_TIME, querySplitParam.getBeginTime())
                .replaceDate(SqlTemplateParamEnum.END_TIME, querySplitParam.getEndTime()).getSql();
            Document query = Document.parse(template);
            query.remove(MongoUtil.DB);
            MongoUtil.appendLimit(query, monitorProperties.getMaxDetailCount(monitorType) + 1);
            return query;
        }

    }

}
