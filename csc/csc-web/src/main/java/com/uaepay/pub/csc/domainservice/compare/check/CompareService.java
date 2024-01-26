package com.uaepay.pub.csc.domainservice.compare.check;

import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;

/**
 * 比对服务
 * 
 * @author zc
 */
public interface CompareService {

    /**
     * 比对
     * 
     * @param srcRows
     *            源数据
     * @param targetRows
     *            目标数据
     * @param checkExpression
     *            校验表达式
     * @return 错误明细列表
     */
    CompareResult compare(SrcRows srcRows, TargetRows targetRows, String checkExpression);

}
