package com.uaepay.pub.csc.datasource.facade.domain;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.pub.csc.datasource.facade.builder.RowDataListBuilder;

/**
 * 行数据列表
 *
 * @author zc
 * @see RowDataListBuilder
 */
public class RowDataList {

    List<String> columnNames;

    List<List<Object>> rowList;

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<List<Object>> getRowList() {
        return rowList;
    }

    public void setRowList(List<List<Object>> rowList) {
        this.rowList = rowList;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.JSON_STYLE).setExcludeFieldNames("rowList")
            .append("listSize", rowList != null ? rowList.size() : 0).toString();
    }
}
