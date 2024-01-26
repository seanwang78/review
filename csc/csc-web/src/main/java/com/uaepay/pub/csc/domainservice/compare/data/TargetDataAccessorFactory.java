package com.uaepay.pub.csc.domainservice.compare.data;

/**
 * 目标数据访问器工厂
 * @author zc
 */
public interface TargetDataAccessorFactory {

    TargetDataAccessor create(String datasourceCode, String sqlTemplate, String relateField, String shardingExpression);

}
