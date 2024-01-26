package com.uaepay.pub.csc.domain.compare;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.pub.csc.domain.data.RowData;

/**
 * 目标数据
 * 
 * @author zc
 */
public class TargetRows {

    public TargetRows(List<RowData> rows) {
        groupMap = new LinkedHashMap<>();
        for (RowData row : rows) {
            String relateValue = row.getStringRelateValue();
            if (!groupMap.containsKey(relateValue)) {
                groupMap.put(relateValue, new GroupRows(relateValue));
            }
            groupMap.get(relateValue).getRows().add(row);
        }
    }

    private final Map<String, GroupRows> groupMap;

    public int size() {
        return groupMap.size();
    }

    /**
     * 剔除一个关联值对应的目标数据组
     * 
     * @param relateValue
     *            关联值
     * @return
     */
    public GroupRows removeRelateGroup(String relateValue) {
        return groupMap.remove(relateValue);
    }

    /**
     * 获取剩余的数据
     * 
     * @return
     */
    public List<GroupRows> getRemainGroups() {
        return new ArrayList<>(groupMap.values());
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .setExcludeFieldNames("relateValueMap").toString();
    }
}
