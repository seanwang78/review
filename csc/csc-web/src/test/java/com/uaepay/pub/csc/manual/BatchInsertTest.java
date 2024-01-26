package com.uaepay.pub.csc.manual;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareDetailMapper;
import com.uaepay.pub.csc.service.facade.enums.CompareStatusEnum;
import com.uaepay.pub.csc.test.base.ManualTestBase;

@Disabled
public class BatchInsertTest extends ManualTestBase {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Resource
    CompareDetailMapper compareDetailMapper;

    AtomicInteger counter = new AtomicInteger();

    @Test
    public void test1() {
        List<CompareDetail> details = new ArrayList<>();
        for (int i = 0; i != 100; i++) {
            details.add(template());
        }
        for (CompareDetail detail : details) {
            compareDetailMapper.insertSelective(detail);
            System.out.println("inserted");
        }
    }

    @Test
    public void test2() {
        List<CompareDetail> details = new ArrayList<>();
        for (int i = 0; i != 100; i++) {
            details.add(template());
        }
        int i = 0;
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            for (CompareDetail detail : details) {
                sqlSession.insert("com.uaepay.pub.csc.core.dal.mapper.CompareDetailMapper.insertSelective", detail);
                logger.info("inserted {}", ++i);
            }
            sqlSession.commit();
        }
        logger.info("done");
    }

    private CompareDetail template() {
        CompareDetail result = new CompareDetail();
        result.setCompareStatus(CompareStatusEnum.LACK);
        result.setCompensateFlag(null);
        result.setRelateIdentity(counter.incrementAndGet() + "");
        result.setTaskId(1L);
        return result;
    }
}
