package com.uaepay.pub.csc.ext.integration.impl;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.datasource.facade.ApiDataSourceFacade;
import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;
import com.uaepay.pub.csc.datasource.facade.request.QueryDataRequest;
import com.uaepay.pub.csc.datasource.facade.response.QueryDataResponse;
import com.uaepay.pub.csc.domain.monitor.ApiQueryConfig;
import com.uaepay.pub.csc.ext.integration.ApiDataSourceClient;
import com.uaepay.pub.csc.ext.integration.base.BaseClient;

/**
 * @author zc
 */
@Service
public class ApiDataSourceClientImpl extends BaseClient implements ApiDataSourceClient {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private ConsumerConfig consumerConfig;

    private final ConcurrentHashMap<String, ApiDataSourceFacade> serviceMap = new ConcurrentHashMap<>();

    @Override
    public RowDataList queryData(Date beginTime, Date endTime, ApiQueryConfig config) {
        QueryDataRequest request = buildRequest(beginTime, endTime, config);
        logger.info("数据查询请求: {}", request);
        ApiDataSourceFacade dataSourceFacade = getOrCreateFacade(config.getAppCode());
        QueryDataResponse response = dataSourceFacade.queryData(request);
        logger.info("数据查询响应: {}", response);
        if (response.getApplyStatus() != ApplyStatusEnum.SUCCESS) {
            throw new ErrorException("数据查询异常: " + response.getMessage());
        }
        return response.getRowDataList();
    }

    private QueryDataRequest buildRequest(Date beginTime, Date endTime, ApiQueryConfig config) {
        QueryDataRequest result = new QueryDataRequest();
        result.setClientId(clientId);
        result.setBeginTime(beginTime);
        result.setEndTime(endTime);
        result.setDataType(config.getDataType());
        result.setQueryParam(config.getQueryParam());
        return result;
    }

    public ApiDataSourceFacade getOrCreateFacade(String appCode) {
        ApiDataSourceFacade service = serviceMap.get(appCode);
        if (service != null) {
            return service;
        }
        synchronized (serviceMap) {
            service = serviceMap.get(appCode);
            if (service != null) {
                return service;
            }
            service = buildService(appCode);
            serviceMap.put(appCode, service);
        }
        return service;
    }

    public ApiDataSourceFacade buildService(String appCode) {
        ReferenceConfig<ApiDataSourceFacade> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setConsumer(consumerConfig);
        reference.setInterface(ApiDataSourceFacade.class.getName());
        reference.setVersion(appCode);
        return reference.get();
    }

}
