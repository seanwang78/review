package com.uaepay.pub.csc.domainservice.compare.data.mysql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.data.*;
import com.uaepay.pub.csc.domain.enums.SqlTemplateParamEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;
import com.uaepay.pub.csc.domainservice.compare.data.impl.AbstractSrcDataIterator;
import com.uaepay.pub.csc.domainservice.data.JdbcTemplateFactory;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.data.mysql.MysqlSqlTemplateReplacer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * mysql默认源数据迭代器工厂
 * 
 * @author zc
 */
@Service
public class MysqlSrcDataIteratorFactory {

    @Autowired
    private JdbcTemplateFactory jdbcTemplateFactory;

    @Autowired
    private CompareProperties compareProperties;

    public SrcDataIterator create(MySqlDataSourceConfig dataSourceConfig, String sqlTemplate, int splitMinutes,
        String relateField, QueryParam queryParam) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.getOrCreate(dataSourceConfig);
        MysqlSrcDataIterator iterator = new MysqlSrcDataIterator(sqlTemplate, splitMinutes, relateField,
            compareProperties.getQueryPageSize(), queryParam);
        iterator.setJdbcTemplate(jdbcTemplate);
        return iterator;
    }

    @Slf4j
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class MysqlSrcDataIterator extends AbstractSrcDataIterator {

        public MysqlSrcDataIterator(String sqlTemplate, int splitMinutes, String relateField, int pageSize,
            QueryParam queryParam) {
            super(sqlTemplate, splitMinutes, relateField, pageSize, queryParam);
        }

        JdbcTemplate jdbcTemplate;

        @Override
        protected void init() {
            ParameterValidate.assertTrue("{begin} not exist", SqlTemplateParamEnum.BEGIN_TIME.find(sqlTemplate));
            ParameterValidate.assertTrue("{end} not exist", SqlTemplateParamEnum.END_TIME.find(sqlTemplate));
            ParameterValidate.assertTrue("{offset} not exist", SqlTemplateParamEnum.OFFSET.find(sqlTemplate));
            ParameterValidate.assertTrue("{count} not exist", SqlTemplateParamEnum.COUNT.find(sqlTemplate));
        }

        @Override
        protected List<QuerySplitParam> splitQueryParam() {
            return QueryParamSplitter.splitQueryParamByTime(queryParam, splitMinutes);
        }

        @Override
        protected SrcRows query(QuerySplitParam querySplitParam, int page, int pageSize) {
            String sql = buildSql(querySplitParam, page, pageSize);
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
            ColumnData columnData = ColumnData.from(rowSet.getMetaData(), relateField);
            return new SrcRows(RowDataConverter.from(rowSet, columnData));
        }

        private String buildSql(QuerySplitParam querySplitParam, int page, int pageSize) {
            int offset = (page - 1) * pageSize;
            return new MysqlSqlTemplateReplacer(sqlTemplate)
                .replaceDate(SqlTemplateParamEnum.BEGIN_TIME, querySplitParam.getBeginTime())
                .replaceDate(SqlTemplateParamEnum.END_TIME, querySplitParam.getEndTime())
                .replaceNumber(SqlTemplateParamEnum.OFFSET, offset).replaceNumber(SqlTemplateParamEnum.COUNT, pageSize)
                .getSql();
        }

    }

}
