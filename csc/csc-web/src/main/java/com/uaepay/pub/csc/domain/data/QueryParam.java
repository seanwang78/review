package com.uaepay.pub.csc.domain.data;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 源数据查询参数
 * 
 * @author zc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryParam {

    private Date beginTime;

    private Date endTime;

    public QueryParam beginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public QueryParam endTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

}
