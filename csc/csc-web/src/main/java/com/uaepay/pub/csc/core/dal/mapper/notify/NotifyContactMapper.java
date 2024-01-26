package com.uaepay.pub.csc.core.dal.mapper.notify;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;

public interface NotifyContactMapper {

    List<NotifyContact> selectByDefineId(@Param("defineType") DefineTypeEnum defineType,
        @Param("defineId") long defineId);

    // int deleteByPrimaryKey(Long contactId);

    // int insert(NotifyContact record);

    int insertSelective(NotifyContact record);

    // NotifyContact selectByPrimaryKey(Long contactId);

    // int updateByPrimaryKeySelective(NotifyContact record);

    // int updateByPrimaryKey(NotifyContact record);
}