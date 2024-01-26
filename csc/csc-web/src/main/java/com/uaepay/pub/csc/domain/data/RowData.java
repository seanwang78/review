package com.uaepay.pub.csc.domain.data;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;

import lombok.Data;

/**
 * @author zc
 */
@Data
public class RowData {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssX");

    public RowData(ColumnData columnData, List<Object> row) {
        this.columnData = columnData;
        this.row = row;
    }

    /** 列数据 */
    ColumnData columnData;

    /** 数据列表 */
    List<Object> row;

    /**
     * 获取关联值
     * 
     * @return 关联值
     */
    public Object getRelateValue() {
        return row.get(columnData.getRelateFieldIndex());
    }

    /**
     * 获取关联值
     *
     * @return 关联值字符串
     */
    public String getStringRelateValue() {
        Object result = getRelateValue();
        return result != null ? result.toString() : null;
    }

    /**
     * 判断是否存在列
     * 
     * @param columnName
     *            列名
     * @return 是否存在
     */
    public boolean containsItem(String columnName) {
        return columnData.containsColumn(columnName);
    }

    /**
     * 获取指定列名的数据
     * 
     * @param columnName
     *            列名
     * @return 数据，如果不存在，返回null
     */
    public Object getItem(String columnName) {
        return row.get(columnData.getIndex(columnName));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ColumnData.Column column : columnData.getColumnInfos()) {
            sb.append(column.getName()).append("=").append(convertString(row.get(column.getIndex()))).append(", ");
        }
        if (columnData.getColumnInfos().size() > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    private String convertString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return DATE_FORMAT.format((Date)value);
        }
        return value.toString();
    }

}
