package com.uaepay.pub.csc.domainservice.compare.repository;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;

/**
 * @author zc
 */
public interface CompareDefineRepository {

    /**
     * 根据定义id获取定义
     * 
     * @param defineId
     * @return
     */
    CompareDefine getByDefineId(Long defineId);

}
