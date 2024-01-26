package com.uaepay.pub.csc.test.mocker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.JobProgressMapper;
import com.uaepay.pub.csc.test.builder.JobProgressBuilder;
import com.uaepay.pub.csc.test.dal.JobProgressTestMapper;

@Service
public class JobProgressMocker {

    @Autowired
    JobProgressMapper jobProgressMapper;

    @Autowired
    JobProgressTestMapper jobProgressTestMapper;

    /**
     * mock只有一个job的配置
     * 
     * @see JobProgressBuilder
     */
    public JobProgress mockSingle(JobProgressBuilder builder) {
        JobProgress result = builder.build();
        clear().mock(result);
        return result;
    }

    public JobProgressMocker mock(JobProgress progress) {
        jobProgressMapper.insertSelective(progress);
        System.out.printf("mock进度: %s\n", progress);
        return this;
    }

    public JobProgressMocker clear() {
        int count = jobProgressTestMapper.deleteAll();
        System.out.printf("删除所有进度: count=%d\n", count);
        return this;
    }

}
