package com.uaepay.pub.csc.domain.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;

/**
 * 监控结果
 * 
 * @author zc
 */
public class MonitorResult {

    final List<RowData> details = new ArrayList<>();

    /** 是否全部数据 */
    boolean all = true;

    /**
     * 根据明细数量判断通知级别
     * 
     * @param normalCount
     *            小于等于此值不通知
     * @param urgentCount
     *            小于此值，大于normalCount，普通通知；大于等于此值紧急通知
     * @return 报警级别
     */
    public AlarmLevelEnum levelByCount(int normalCount, int urgentCount) {
        if (details.size() <= normalCount) {
            return AlarmLevelEnum.IGNORE;
        } else if (details.size() < urgentCount) {
            return AlarmLevelEnum.NORMAL;
        } else {
            return AlarmLevelEnum.URGENT;
        }
    }

    /**
     * 合并
     *
     * @param result
     *            合并结果
     * @return 是否全部的明细数据，当放不下的时候返回false
     */
    public boolean merge(QueryRows result, int maxCount, SplitStrategyEnum splitStrategy) {
        if (result == null || result.size() == 0) {
            return all;
        }
        // 当前没数据和拼接策略是等价的
        if (details.isEmpty() || splitStrategy == SplitStrategyEnum.UNION) {
            for (RowData row : result.getRows()) {
                if (details.size() >= maxCount) {
                    all = false;
                    break;
                }
                details.add(row);
            }
            return all;
        } else {
            throw new ErrorException("不支持合并操作");
        }
    }

    public List<RowData> getDetails() {
        return details;
    }

    public int size() {
        return details.size();
    }

    public boolean isAll() {
        return all;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }

}
