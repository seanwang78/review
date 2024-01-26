package com.uaepay.pub.csc.ext.service;

import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.common.template.AbstractProcessTemplate;
import com.uaepay.basis.beacon.service.facade.domain.request.OperateRequest;
import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.service.facade.CompareDefineManagerFacade;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;

/**
 * @author zc
 */
@Service
public class CompareDefineManagerFacadeImpl implements CompareDefineManagerFacade {

    @Autowired
    DataSourceConfigFactory dataSourceConfigFactory;

    @Override
    public ObjectQueryResponse<List<String>> getDataSourceCodeList(OperateRequest request) {
        return new AbstractProcessTemplate<OperateRequest, ObjectQueryResponse<List<String>>>() {
            @Override
            protected String getServiceName() {
                return "查询数据源代码";
            }

            @Override
            protected ObjectQueryResponse<List<String>> createEmptyResponse() {
                return new ObjectQueryResponse<>();
            }

            @Override
            protected void validate(OperateRequest request) {}

            @Override
            protected void process(OperateRequest request, ObjectQueryResponse<List<String>> response) {
                response.success(dataSourceConfigFactory.getCodeList());
            }
        }.process(request);
    }

    @Override
    public ObjectQueryResponse<Map<String, List<String>>> getDataSourceMap(OperateRequest request) {
        return new AbstractProcessTemplate<OperateRequest,
            ObjectQueryResponse<Map<String, List<String>>>>() {
            @Override
            protected String getServiceName() {
                return "查询数据源map";
            }

            @Override
            protected ObjectQueryResponse<Map<String, List<String>>> createEmptyResponse() {
                return new ObjectQueryResponse<>();
            }

            @Override
            protected void validate(OperateRequest request) {}

            @Override
            protected void process(OperateRequest request,
                ObjectQueryResponse<Map<String, List<String>>> response) {
                response.success(dataSourceConfigFactory.getCodeMap());
            }
        }.process(request);
    }

}
