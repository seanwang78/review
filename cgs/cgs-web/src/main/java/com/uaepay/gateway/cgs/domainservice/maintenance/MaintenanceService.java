package com.uaepay.gateway.cgs.domainservice.maintenance;

/**
 * @author zc
 */
public interface MaintenanceService {

    /**
     * 是否在维护中
     *
     * @return 是否
     */
    boolean isUnderMaintenance();

    /**
     * 返回维护中的响应字符串
     *
     * @return 维护中响应
     */
    String getResponse();

}