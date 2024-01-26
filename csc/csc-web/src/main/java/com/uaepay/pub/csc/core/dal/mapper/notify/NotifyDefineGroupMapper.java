package com.uaepay.pub.csc.core.dal.mapper.notify;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyDefineGroup;

public interface NotifyDefineGroupMapper {
//    int deleteByPrimaryKey(Long relateId);

//    int insert(NotifyDefineGroup record);

    int insertSelective(NotifyDefineGroup record);

//    NotifyDefineGroup selectByPrimaryKey(Long relateId);

//    int updateByPrimaryKeySelective(NotifyDefineGroup record);

//    int updateByPrimaryKey(NotifyDefineGroup record);
}