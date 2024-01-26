package com.uaepay.pub.csc.domain.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.service.facade.enums.CompareStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;

import lombok.Data;

/**
 * 错误明细
 * 
 * @author zc
 */
@Data
public class ErrorDetail {

    public static ErrorDetail lackTarget(GroupRows srcGroup) {
        ErrorDetail result = new ErrorDetail();
        result.setRelateIdentity(srcGroup.getRelateValue());
        result.setSrcDataList(srcGroup.getRows());
        result.setCompareStatus(CompareStatusEnum.LACK);
        result.setErrorMessages(Collections.singletonList("lack target data"));
        return result;
    }

    public static ErrorDetail lackSrc(GroupRows targetGroup) {
        ErrorDetail result = new ErrorDetail();
        result.setRelateIdentity(targetGroup.getRelateValue());
        result.setTargetDataList(targetGroup.getRows());
        result.setCompareStatus(CompareStatusEnum.LACK);
        result.setErrorMessages(Collections.singletonList("lack source data"));
        return result;
    }

    public static ErrorDetail mismatch(GroupRows srcGroup, GroupRows targetGroup, Collection<String> errorMessages) {
        ErrorDetail result = new ErrorDetail();
        result.setRelateIdentity(srcGroup.getRelateValue());
        result.setSrcDataList(srcGroup.getRows());
        result.setTargetDataList(targetGroup.getRows());
        result.setCompareStatus(CompareStatusEnum.MISMATCH);
        if (errorMessages == null) {
            result.setErrorMessages(new ArrayList<>());
        } else {
            result.setErrorMessages(new ArrayList<>(errorMessages));
        }
        return result;
    }

    String relateIdentity;

    List<RowData> srcDataList;

    List<RowData> targetDataList;

    CompareStatusEnum compareStatus;

    List<String> errorMessages;

    CompensateFlagEnum compensateFlag;

    /** 补单通知状态，可空 */
    String notifyStatus;

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getErrorMessage() {
        if (errorMessages.size() == 0) {
            return null;
        }
        return StringUtils.join(errorMessages, ", ");
    }

}
