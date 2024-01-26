package com.uaepay.pub.csc.core.dal.mapper.compare;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;

public interface CompareDefineMapper {

    int insertSelective(CompareDefine record);

    CompareDefine selectByPrimaryKey(Long defineId);

    int updateByPrimaryKeySelective(CompareDefine record);

}