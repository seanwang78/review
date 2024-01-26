package com.uaepay.pub.csc.domainservice.logmonitor.repository.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogStatMapper;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.LogStatRepository;
import com.uaepay.pub.csc.service.facade.enums.LogStatStatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author cyx
 */
@Slf4j
@Repository
public class LogStatRepositoryImpl implements LogStatRepository {

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    LogStatMapper logStatMapper;

    @Override
    public void saveLogStats(List<LogStat> logStatList) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            for (LogStat detail : logStatList) {
                detail.setReturnMsg(StringUtils.abbreviate(detail.getReturnMsg(), 250));
                session.insert("com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogStatMapper.insertSelective", detail);
            }
            session.commit();
        }
    }

    @Override
    public List<LogStat> selectToRetry(Date begin, Date end, Long id, Integer pageSize) {
        return logStatMapper.selectToRetry(begin, end, id, pageSize);
    }

    @Override
    public int updateManualConfirm(List<Long> idList, String operator) {
        if (CollectionUtils.isEmpty(idList)) {
            return 0;
        }
        return logStatMapper.updateStatus(idList, LogStatStatusEnum.MANUAL.getCode(), operator);
    }

    @Override
    public int updateRetrySuccess(List<Long> idList, String operator) {
        if (CollectionUtils.isEmpty(idList)) {
            return 0;
        }
        return logStatMapper.updateStatus(idList, LogStatStatusEnum.RETRY.getCode(), operator);
    }
}
