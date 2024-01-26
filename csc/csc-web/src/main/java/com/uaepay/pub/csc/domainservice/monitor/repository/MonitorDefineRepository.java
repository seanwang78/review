package com.uaepay.pub.csc.domainservice.monitor.repository;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;

/**
 * @author zc
 */
public interface MonitorDefineRepository {

    /**
     * 根据定义id获取定义
     * 
     * @param defineId
     *            定义id
     * @return
     */
    MonitorDefine getByDefineId(Long defineId);

}
