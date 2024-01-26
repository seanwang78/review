package com.uaepay.pub.csc.domainservice.compare.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareDefineMapper;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareDefineRepository;

@Repository
public class CompareDefineRepositoryImpl implements CompareDefineRepository {

    @Autowired
    CompareDefineMapper compareDefineMapper;

    @Override
    public CompareDefine getByDefineId(Long defineId) {
        return compareDefineMapper.selectByPrimaryKey(defineId);
    }

}
