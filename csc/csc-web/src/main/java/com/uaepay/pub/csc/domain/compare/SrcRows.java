package com.uaepay.pub.csc.domain.compare;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.pub.csc.domain.data.RowData;

/**
 * 源数据
 * 
 * @author zc
 */
public class SrcRows {

    public SrcRows(List<RowData> rows) {
        groupMap = new LinkedHashMap<>();
        for (RowData row : rows) {
            String relateValue = row.getStringRelateValue();
            if (!groupMap.containsKey(relateValue)) {
                groupMap.put(relateValue, new GroupRows(relateValue));
            }
            groupMap.get(relateValue).getRows().add(row);
        }
    }

    private Map<String, GroupRows> groupMap;

    public boolean isEmpty() {
        return groupMap.isEmpty();
    }

    public int size() {
        return groupMap.size();
    }

    /**
     * 获取关联字段的值列表
     * 
     * @return 值列表
     */
    public List<String> getRelateValues() {
        return groupMap.keySet().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public Map<String, GroupRows> getGroupMap() {
        return groupMap;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
