package com.uaepay.pub.csc.domain.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.pub.csc.domain.data.RowData;

/**
 * 查询结果
 * 
 * @author zc
 */
public class QueryRows {

    public QueryRows(List<RowData> rows) {
        this.rows = rows != null ? rows : new ArrayList<>();
    }

    private final List<RowData> rows;

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public int size() {
        return rows.size();
    }

    public List<RowData> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
