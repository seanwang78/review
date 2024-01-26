package com.uaepay.pub.csc.test.mocker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareDefineMapper;
import com.uaepay.pub.csc.test.builder.CompareDefineBuilder;
import com.uaepay.pub.csc.test.dal.CompareTestMapper;

@Service
public class CompareDefineMocker {

    @Autowired
    private CompareTestMapper compareTestMapper;

    @Autowired
    private CompareDefineMapper compareDefineMapper;

    public long mock(CompareDefineBuilder compareDefineBuilder) {
        CompareDefine define = compareDefineBuilder.build();
        Long defineId = compareTestMapper.selectDefineByName(define.getDefineName());
        if (defineId != null) {
            int delete = compareTestMapper.deleteDefineById(defineId);
            System.out.printf("删除定义: defineName=%s, count=%d\n", define.getDefineName(), delete);
            delete = compareTestMapper.deleteScheduleByDefineId(defineId);
            System.out.printf("删除计划: defineName=%s, count=%d\n", define.getDefineName(), delete);
        }
        compareDefineMapper.insertSelective(define);
        System.out.printf("准备定义: %s, id: %d\n", define.getDefineName(), define.getDefineId());
        return define.getDefineId();
    }

}
