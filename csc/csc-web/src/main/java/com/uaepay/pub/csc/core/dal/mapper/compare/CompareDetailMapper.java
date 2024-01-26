package com.uaepay.pub.csc.core.dal.mapper.compare;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;

public interface CompareDetailMapper {
    int deleteByPrimaryKey(Long detailId);

    int insert(CompareDetail record);

    int insertSelective(CompareDetail record);

    CompareDetail selectByPrimaryKey(Long detailId);

    int updateByPrimaryKeySelective(CompareDetail record);

    int updateByPrimaryKey(CompareDetail record);

    List<CompareDetail> listWaitCompensateDetails(Long taskId);

    int updateDetailCompensate(@Param("detailId") Long detailId, @Param("oldStatus") CompensateFlagEnum oldStatus,
        @Param("newStatus") CompensateFlagEnum newStatus);

}