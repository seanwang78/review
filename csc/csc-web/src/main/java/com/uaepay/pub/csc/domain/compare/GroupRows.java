package com.uaepay.pub.csc.domain.compare;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.uaepay.pub.csc.domain.data.RowData;

import lombok.Data;

/**
 * 行分组
 * 
 * @author zc
 */
@Data
public class GroupRows {

    public GroupRows(String relateValue) {
        this.relateValue = relateValue;
        this.rows = new ArrayList<>(1);
    }

    public GroupRows(String relateValue, List<RowData> rows) {
        this.relateValue = relateValue;
        this.rows = rows;
    }

    private final String relateValue;

    private final List<RowData> rows;

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(rows);
    }

    public String getRelateValue() {
        return relateValue;
    }

    public List<RowData> getRows() {
        return rows;
    }

}
