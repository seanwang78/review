package com.uaepay.pub.csc.datasource.facade.builder;

import java.util.ArrayList;
import java.util.Arrays;

import com.uaepay.pub.csc.datasource.facade.domain.RowDataList;

/**
 * 行数据列表建造器
 * 
 * @author zc
 */
public class RowDataListBuilder {

    RowDataList result = new RowDataList();

    public RowDataListBuilder columns(String... columns) {
        result.setColumnNames(Arrays.asList(columns));
        return this;
    }

    public RowDataListBuilder addRow(Object... dataList) {
        if (result.getRowList() == null) {
            result.setRowList(new ArrayList<>());
        }
        result.getRowList().add(Arrays.asList(dataList));
        return this;
    }

    public RowDataList build() {
        return result;
    }

}
