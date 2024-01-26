package com.uaepay.pub.csc.core.dal.mapper.monitor;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;

public interface MonitorTaskDetailMapper {
    int deleteByPrimaryKey(Long detailId);

    int insert(MonitorTaskDetail record);

    int insertSelective(MonitorTaskDetail record);

    MonitorTaskDetail selectByPrimaryKey(Long detailId);

    int updateByPrimaryKeySelective(MonitorTaskDetail record);

    int updateByPrimaryKey(MonitorTaskDetail record);
}