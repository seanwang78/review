package com.uaepay.pub.csc.domainservice.logmonitor.repository;

import java.util.Date;
import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;

/**
 * @author zc
 */
public interface LogStatRepository {

    /**
     * 保存日志统计信息
     * 
     * @param logStatList
     *            日志统计列表
     */
    void saveLogStats(List<LogStat> logStatList);

    /**
     * 查询待重试日志统计列表
     * 
     * @param begin
     *            开始时间
     * @param end
     *            结束时间
     * @param id
     *            起始id
     * @param pageSize
     *            每页数量
     * @return 日志统计列表
     */
    List<LogStat> selectToRetry(Date begin, Date end, Long id, Integer pageSize);

    /**
     * 更新为人工确认
     * 
     * @param idList
     *            待更新列表
     * @param operator
     *            操作员
     * @return 更新行数
     */
    int updateManualConfirm(List<Long> idList, String operator);

    /**
     * 更新为重试成功
     * 
     * @param idList
     *            待更新列表
     * @param operator
     *            操作员
     * @return 更新行数
     */
    int updateRetrySuccess(List<Long> idList, String operator);

}
