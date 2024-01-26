package com.uaepay.pub.csc.domainservice.compare.compensate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.common.util.VelocityUtil;
import com.uaepay.pub.csc.core.common.util.CscUtil;
import com.uaepay.pub.csc.domain.compare.ErrorDetail;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.enums.TaskErrorCodeEnum;
import com.uaepay.pub.csc.domainservice.compare.compensate.CompensateMatcher;
import com.uaepay.pub.csc.domainservice.compare.compensate.CompensateService;
import com.uaepay.pub.csc.service.facade.enums.CompareStatusEnum;
import com.uaepay.pub.csc.service.facade.enums.CompensateFlagEnum;
import com.uaepay.validate.exception.ValidationException;

/**
 * 补单服务默认实现
 * 
 * @author zc
 */
@Service
public class CompensateServiceImpl implements CompensateService {

    private static final String RESULT_MATCHER = "result";

    @Override
    public boolean fillCompensateFlag(ErrorDetail errorDetail, String checkExpression) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(RESULT_MATCHER, new CompensateMatcherImpl(errorDetail));
        try {
            String result = StringUtils.trim(VelocityUtil.getString(checkExpression, params));
            if (StringUtils.isNotEmpty(result)) {
                throw new ErrorException(TaskErrorCodeEnum.COMPENSATE_EXPRESSION_ERROR, result);
            }
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
        return errorDetail.getCompensateFlag() != null;
    }

    public static class CompensateMatcherImpl implements CompensateMatcher {

        public CompensateMatcherImpl(ErrorDetail errorDetail) {
            this.errorDetail = errorDetail;
        }

        ErrorDetail errorDetail;

        @Override
        public void lack() {
            if (errorDetail.getCompareStatus() == CompareStatusEnum.LACK) {
                errorDetail.setCompensateFlag(CompensateFlagEnum.WAIT);
            }
        }

        @Override
        public void lack(String srcField, String srcValues, String... notifyStatus) {
            if (errorDetail.getCompareStatus() != CompareStatusEnum.LACK) {
                return;
            }
            srcField = StringUtils.trim(srcField);
            if (CollectionUtils.isEmpty(errorDetail.getSrcDataList())
                || !errorDetail.getSrcDataList().get(0).getColumnData().containsColumn(srcField)) {
                return;
            }
            List<String> srcValueList = CscUtil.splitString(srcValues);
            for (RowData srcData : errorDetail.getSrcDataList()) {
                String srcValue = srcData.getItem(StringUtils.lowerCase(srcField)) + "";
                // 数据值不在范围内
                if (!srcValueList.contains(srcValue)) {
                    continue;
                }
                errorDetail.setCompensateFlag(CompensateFlagEnum.WAIT);
                if (notifyStatus != null && notifyStatus.length > 0 && StringUtils.isNotBlank(notifyStatus[0])) {
                    errorDetail.setNotifyStatus(StringUtils.trim(notifyStatus[0]));
                }
                break;
            }
        }

        @Override
        public void mismatch(String srcField, String targetField, String srcValues, String targetValues,
            String... notifyStatus) {
            if (errorDetail.getCompareStatus() != CompareStatusEnum.MISMATCH) {
                return;
            }
            srcField = StringUtils.trim(srcField);
            targetField = StringUtils.trim(targetField);
            if (CollectionUtils.isEmpty(errorDetail.getSrcDataList())
                || !errorDetail.getSrcDataList().get(0).getColumnData().containsColumn(srcField)) {
                return;
            }
            if (CollectionUtils.isEmpty(errorDetail.getTargetDataList())
                || !errorDetail.getTargetDataList().get(0).getColumnData().containsColumn(targetField)) {
                return;
            }
            List<String> srcValueList = CscUtil.splitString(srcValues);
            List<String> targetValueList = CscUtil.splitString(targetValues);
            for (RowData srcData : errorDetail.getSrcDataList()) {
                String srcValue = srcData.getItem(StringUtils.lowerCase(srcField)) + "";
                if (!srcValueList.contains(srcValue)) {
                    continue;
                }
                for (RowData targetData : errorDetail.getTargetDataList()) {
                    String targetValue = targetData.getItem(StringUtils.lowerCase(targetField)) + "";
                    // 数据值不在范围内
                    if (targetValueList.contains(targetValue)) {
                        errorDetail.setCompensateFlag(CompensateFlagEnum.WAIT);
                        if (notifyStatus != null && notifyStatus.length > 0
                            && StringUtils.isNotBlank(notifyStatus[0])) {
                            errorDetail.setNotifyStatus(StringUtils.trim(notifyStatus[0]));
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public void mismatchMiss(String srcField, String targetField, String srcValues, String targetValue,
            String... notifyStatus) {
            if (errorDetail.getCompareStatus() != CompareStatusEnum.MISMATCH) {
                return;
            }
            srcField = StringUtils.trim(srcField);
            targetField = StringUtils.trim(targetField);
            targetValue = StringUtils.trim(targetValue);
            if (CollectionUtils.isEmpty(errorDetail.getSrcDataList())
                || !errorDetail.getSrcDataList().get(0).getColumnData().containsColumn(srcField)) {
                return;
            }
            if (CollectionUtils.isEmpty(errorDetail.getTargetDataList())
                || !errorDetail.getTargetDataList().get(0).getColumnData().containsColumn(targetField)) {
                return;
            }
            List<String> srcValueList = CscUtil.splitString(srcValues);
            for (RowData srcData : errorDetail.getSrcDataList()) {
                String srcValue = srcData.getItem(StringUtils.lowerCase(srcField)) + "";
                if (!srcValueList.contains(srcValue)) {
                    continue;
                }
                boolean exist = false;
                for (RowData targetData : errorDetail.getTargetDataList()) {
                    String target = targetData.getItem(StringUtils.lowerCase(targetField)) + "";
                    if (StringUtils.equals(target, targetValue)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    errorDetail.setCompensateFlag(CompensateFlagEnum.WAIT);
                    if (notifyStatus != null && notifyStatus.length > 0 && StringUtils.isNotBlank(notifyStatus[0])) {
                        errorDetail.setNotifyStatus(StringUtils.trim(notifyStatus[0]));
                    }
                    break;
                }
            }
        }
    }
}
