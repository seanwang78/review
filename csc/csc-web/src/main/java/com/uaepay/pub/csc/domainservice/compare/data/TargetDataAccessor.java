package com.uaepay.pub.csc.domainservice.compare.data;

import java.util.List;

import com.uaepay.pub.csc.domain.compare.TargetRows;

/**
 * 目标数据访问器
 * 
 * @author zc
 */
public interface TargetDataAccessor {

    /**
     * 查询目标数据
     * 
     * @param relateValues
     *            关联字段值列表
     * @return 目标数据
     */
    TargetRows queryTargetRows(List<String> relateValues);

}
