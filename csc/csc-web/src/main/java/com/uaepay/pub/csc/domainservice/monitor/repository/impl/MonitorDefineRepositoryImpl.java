package com.uaepay.pub.csc.domainservice.monitor.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorDefineMapper;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorDefineRepository;

/**
 * @author zc
 */
@Repository
public class MonitorDefineRepositoryImpl implements MonitorDefineRepository {

    @Autowired
    MonitorDefineMapper monitorDefineMapper;

    @Override
    public MonitorDefine getByDefineId(Long defineId) {
        return monitorDefineMapper.selectByPrimaryKey(defineId);
    }

}
