package com.uaepay.pub.csc.domain.data;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 源数据查询参数
 * 
 * @author zc
 */
@Data
@Builder
public class QuerySplitParam {

    /** 开始时间 */
    private Date beginTime;

    /** 结束时间 */
    private Date endTime;

    /** 分表后缀 */
    private String shardingSuffix;

}
