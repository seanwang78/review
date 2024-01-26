package com.uaepay.pub.csc.ext.integration.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.uaepay.pub.csc.ext.integration.base.BaseClient;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;
import com.uaepay.pub.csc.compensation.facade.CompensationFacade;
import com.uaepay.pub.csc.compensation.facade.domain.CompensateDetail;
import com.uaepay.pub.csc.compensation.facade.request.CompensateSingleRequest;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.domain.compensate.CompensateConfig;
import com.uaepay.pub.csc.ext.integration.CompensationClient;

/**
 * @author zc
 */
@Service
public class CompensationClientImpl extends BaseClient implements CompensationClient {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private ConsumerConfig consumerConfig;

    private final ConcurrentHashMap<String, CompensationFacade> serviceMap = new ConcurrentHashMap<>();

    @Override
    public CommonResponse compensateSingle(CompareDetail detail, CompensateConfig config) {
        CompensateSingleRequest request = buildSingleRequest(detail, config);
        logger.info("补单请求: {}", request);
        CompensationFacade compensationFacade = getOrCreateFacade(config.getAppCode());
        CommonResponse response = compensationFacade.applySingle(request);
        logger.info("补单响应: {}", response);
        return response;
    }

    private CompensateSingleRequest buildSingleRequest(CompareDetail detail, CompensateConfig config) {
        CompensateSingleRequest result = new CompensateSingleRequest();
        result.setTaskId(detail.getTaskId());
        result.setNotifyType(config.getNotifyType());
        result.setClientId(clientId);

        CompensateDetail cd = new CompensateDetail();
        cd.setOrderNo(detail.getRelateIdentity());
        cd.setCompareStatus(detail.getCompareStatus().getCode());
        String notifyStatus = detail.getExtension(CompareDetail.NOTIFY_STATUS);
        if (notifyStatus != null) {
            cd.setNotifyStatus(notifyStatus);
        }
        result.setDetail(cd);
        return result;
    }

    public CompensationFacade getOrCreateFacade(String appCode) {
        CompensationFacade service = serviceMap.get(appCode);
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

    public CompensationFacade buildService(String appCode) {
        ReferenceConfig<CompensationFacade> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setConsumer(consumerConfig);
        reference.setInterface(CompensationFacade.class.getName());
        reference.setVersion(appCode);
        return reference.get();
    }

}
