package com.uaepay.pub.csc.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.sequenceutil.IDGen;
import com.uaepay.pub.csc.domain.enums.SeqNameEnum;

/**
 * 序列生成服务
 * 
 * @author zc
 */
@Service
public class IdGeneratorService {

    @Autowired
    private IDGen idGen;

    /**
     * 获取下一个序列，仅id
     */
    public long getNextId(SeqNameEnum seqName) {
        return idGen.get(seqName.name());
    }

}
