package com.uaepay.pub.csc.core.dal.mapper.notify;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotifyGroupMapper {
    List<NotifyGroup> selectGroupByDefineId(@Param("defineType") DefineTypeEnum defineType,
                                            @Param("defineId") long defineId);

//    int deleteByPrimaryKey(Long groupId);

//    int insert(NotifyGroup record);

    int insertSelective(NotifyGroup record);

//    NotifyGroup selectByPrimaryKey(Long groupId);

//    int updateByPrimaryKeySelective(NotifyGroup record);

//    int updateByPrimaryKey(NotifyGroup record);
}