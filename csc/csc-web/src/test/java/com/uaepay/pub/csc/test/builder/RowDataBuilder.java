package com.uaepay.pub.csc.test.builder;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;

import com.uaepay.pub.csc.domain.compare.GroupRows;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.RowData;

public class RowDataBuilder {

    private List<ColumnData.Column> columns = new ArrayList<>();
    private String relateField;
    private ColumnData columnData;
    private List<RowData> rows = new ArrayList<>();

    public RowDataBuilder columns(String... columnNames) {
        for (int i = 0; i != columnNames.length; i++) {
            columns.add(new ColumnData.Column(i, columnNames[i]));
        }
        if (relateField != null) {
            columnData = new ColumnData(columns, relateField);
        }
        return this;
    }

    public RowDataBuilder relate(String relateField) {
        this.relateField = relateField;
        if (!columns.isEmpty()) {
            columnData = new ColumnData(columns, relateField);
        }
        return this;
    }

    public RowDataBuilder row(Object... values) {
        rows.add(new RowData(columnData, Arrays.asList(values)));
        return this;
    }

    public List<RowData> build() {
        return rows;
    }

    public GroupRows buildGroupRows() {
        if (rows.size() >= 1) {
            return new GroupRows(rows.get(0).getStringRelateValue(), rows);
        } else {
            throw new IllegalArgumentException("mock数据准备异常");
        }
    }

}
