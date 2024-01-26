package com.uaepay.pub.csc.core.dal.mapper.monitor;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;

public interface MonitorDefineMapper {

    MonitorDefine selectByPrimaryKey(Long defineId);



    int deleteByPrimaryKey(Long defineId);

    int insert(MonitorDefine record);

    int insertSelective(MonitorDefine record);

    int updateByPrimaryKeySelective(MonitorDefine record);

    int updateByPrimaryKeyWithBLOBs(MonitorDefine record);

    int updateByPrimaryKey(MonitorDefine record);
}