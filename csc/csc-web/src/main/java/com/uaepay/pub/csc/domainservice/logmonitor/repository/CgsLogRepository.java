package com.uaepay.pub.csc.domainservice.logmonitor.repository;

import java.util.Date;
import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;

/**
 * @author zc
 */
public interface CgsLogRepository {

    /**
     * 查询异常数据
     * 
     * @param beginTime
     *            开始时间
     * @param endTime
     *            结束时间
     * @return
     */
    List<LogStat> statCgsLog(Date beginTime, Date endTime);

}
