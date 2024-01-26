package com.uaepay.gateway.cgs.domainservice.maintenance.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.uaepay.gateway.cgs.app.facade.domain.ClientServiceResponse;
import com.uaepay.gateway.cgs.configuration.MaintenanceConfiguration;
import com.uaepay.gateway.cgs.domainservice.maintenance.MaintenanceService;
import com.uaepay.gateway.common.facade.enums.GatewayReturnCode;

/**
 * @author zc
 */
@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Resource
    MaintenanceConfiguration maintenanceConfiguration;

    @Override
    public boolean isUnderMaintenance() {
        return maintenanceConfiguration.isEnabled();
    }

    @Override
    public String getResponse() {
        ClientServiceResponse<Void> response =
            ClientServiceResponse.build(GatewayReturnCode.SYSTEM_ERROR, maintenanceConfiguration.getMessage());
        return JSON.toJSONString(response);
    }

}
