package com.uaepay.pub.csc.core.dal.mapper.notify;

import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroupContact;

public interface NotifyGroupContactMapper {
//    int deleteByPrimaryKey(Long relateId);

//    int insert(NotifyGroupContact record);

    int insertSelective(NotifyGroupContact record);

//    NotifyGroupContact selectByPrimaryKey(Long relateId);

//    int updateByPrimaryKeySelective(NotifyGroupContact record);

//    int updateByPrimaryKey(NotifyGroupContact record);
}