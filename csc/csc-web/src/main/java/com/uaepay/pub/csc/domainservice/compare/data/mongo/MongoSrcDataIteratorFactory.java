package com.uaepay.pub.csc.domainservice.compare.data.mongo;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.core.common.util.MongoUtil;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;
import com.uaepay.pub.csc.domainservice.compare.data.impl.AbstractSrcDataIterator;
import com.uaepay.pub.csc.domainservice.data.MongoTemplateFactory;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.data.mongo.MongoSqlTemplateReplacer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * mongo默认源数据迭代器工厂
 * 
 * @author zc
 */
@Service
public class MongoSrcDataIteratorFactory {

    @Autowired
    private MongoTemplateFactory mongoTemplateFactory;

    @Autowired
    private CompareProperties compareProperties;

    public SrcDataIterator create(MongoDataSourceConfig dataSourceConfig, String sqlTemplate, int splitMinutes,
        String relateField, QueryParam queryParam) {
        // 替换特殊占位符，否则解析会报错
        String tempTemplate = new MongoSqlTemplateReplacer(sqlTemplate)
            .replaceNumber(SqlTemplateParamEnum.BEGIN_TIME, 0).replaceNumber(SqlTemplateParamEnum.END_TIME, 0).getSql();
        String databaseName = MongoUtil.extractDatabaseName(tempTemplate, "source sql");
        MongoTemplate mongoTemplate = mongoTemplateFactory.getOrCreate(dataSourceConfig, databaseName);
        MongoSrcDataIterator iterator = new MongoSrcDataIterator(sqlTemplate, splitMinutes, relateField,
            compareProperties.getQueryPageSize(), queryParam);
        iterator.setMongoTemplate(mongoTemplate);
        return iterator;
    }

    @Slf4j
    @Setter
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class MongoSrcDataIterator extends AbstractSrcDataIterator {

        public MongoSrcDataIterator(String sqlTemplate, int splitMinutes, String relateField, int pageSize,
            QueryParam queryParam) {
            super(sqlTemplate, splitMinutes, relateField, pageSize, queryParam);
        }

        MongoTemplate mongoTemplate;

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
            Document query = buildQuery(querySplitParam, page, pageSize);
            List<RowData> rows = MongoUtil.execute(mongoTemplate, query, relateField);
            return new SrcRows(rows);
        }

        private Document buildQuery(QuerySplitParam querySplitParam, int page, int pageSize) {
            int skip = (page - 1) * pageSize;
            String template = new MongoSqlTemplateReplacer(sqlTemplate)
                .replaceDate(SqlTemplateParamEnum.BEGIN_TIME, querySplitParam.getBeginTime())
                .replaceDate(SqlTemplateParamEnum.END_TIME, querySplitParam.getEndTime()).getSql();
            Document query = Document.parse(template);
            query.remove(MongoUtil.DB);
            MongoUtil.appendSkipLimit(query, skip, pageSize);
            return query;
        }

    }

}
