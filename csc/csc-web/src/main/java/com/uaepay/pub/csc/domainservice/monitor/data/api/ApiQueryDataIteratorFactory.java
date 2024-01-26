package com.uaepay.pub.csc.domainservice.monitor.data.api;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.util.JsonUtil;
import com.uaepay.basis.beacon.common.util.ParameterValidate;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.QuerySplitParam;
import com.uaepay.pub.csc.domain.data.RowDataConverter;
import com.uaepay.pub.csc.domain.monitor.ApiQueryConfig;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domainservice.data.QueryParamSplitter;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.ext.integration.ApiDataSourceClient;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;

import lombok.Data;

/**
 * API数据查询迭代器工厂
 * 
 * @author zc
 */
@Service
public class ApiQueryDataIteratorFactory {

    @Autowired
    ApiDataSourceClient apiDataSourceClient;

    public QueryDataIterator create(MonitorDefine define, QueryParam queryParam) {
        return new ApiQueryDataIterator(queryParam, define);
    }

    @Data
    public class ApiQueryDataIterator implements QueryDataIterator {

        public ApiQueryDataIterator(QueryParam queryParam, MonitorDefine define) {
            if (define.getSplitStrategy() == SplitStrategyEnum.NO) {
                this.splitParamsIterator = QueryParamSplitter.splitQueryParamByTime(queryParam, 0).iterator();
            } else {
                this.splitParamsIterator =
                    QueryParamSplitter.splitQueryParamByTime(queryParam, define.getSplitMinutes()).iterator();
            }
            ParameterValidate.assertTrue("query template not json format",
                JsonUtil.canParseToJson(define.getQueryTemplate()));
            this.queryConfig = JsonUtil.parseObjectMute(define.getQueryTemplate(), ApiQueryConfig.class);
            this.monitorType = define.getMonitorType();
            this.keyField = define.getKeyField();
            validate();
        }

        Iterator<QuerySplitParam> splitParamsIterator;
        ApiQueryConfig queryConfig;
        MonitorTypeEnum monitorType;
        String keyField;

        protected void validate() {
            ParameterValidate.assertTrue("appCode not exist", StringUtils.isNotBlank(queryConfig.getAppCode()));
        }

        @Override
        public boolean hasNext() {
            return splitParamsIterator != null && splitParamsIterator.hasNext();
        }

        @Override
        public QueryRows next() {
            QuerySplitParam params = splitParamsIterator.next();
            RowDataList rowDataList =
                apiDataSourceClient.queryData(params.getBeginTime(), params.getEndTime(), queryConfig);
            ColumnData columnData = ColumnData.fromRowDataList(rowDataList, keyField, false);
            return new QueryRows(RowDataConverter.fromRowDataList(rowDataList, columnData));
        }

    }

}
