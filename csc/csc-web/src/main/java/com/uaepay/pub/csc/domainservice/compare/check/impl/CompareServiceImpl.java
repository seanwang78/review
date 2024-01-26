package com.uaepay.pub.csc.domainservice.compare.check.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.common.util.VelocityUtil;
import com.uaepay.pub.csc.domain.compare.*;
import com.uaepay.pub.csc.domain.enums.TaskErrorCodeEnum;
import com.uaepay.pub.csc.domainservice.compare.check.CompareService;
import com.uaepay.validate.exception.ValidationException;

/**
 * @author zc
 */
@Service
public class CompareServiceImpl implements CompareService {

    private static final String COMPARE_DATA = "data";

    @Override
    public CompareResult compare(SrcRows srcRows, TargetRows targetRows, String checkExpression) {
        List<ErrorDetail> lackDetails = new ArrayList<>(), mismatchDetails = new ArrayList<>();
        int passCount = 0;
        Map<String, GroupRows> srcGroupMap = srcRows.getGroupMap();
        Iterator<Map.Entry<String, GroupRows>> it = srcGroupMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, GroupRows> entry = it.next();
            GroupRows srcGroup = entry.getValue();
            GroupRows targetGroup = targetRows.removeRelateGroup(entry.getKey());
            // 源数据不可能为空
            if (targetGroup == null) {
                // 少目标数据
                lackDetails.add(ErrorDetail.lackTarget(srcGroup));
            } else {
                // 都有数据，来个笛卡尔积
                CompareData compareData = new CompareData(srcGroup, targetGroup);
                checkExpression(compareData, checkExpression);
                if (CollectionUtils.isNotEmpty(compareData.getErrorMessages())) {
                    mismatchDetails.add(ErrorDetail.mismatch(srcGroup, targetGroup, compareData.getErrorMessages()));
                } else {
                    passCount++;
                }
            }
        }
        for (GroupRows remainGroup : targetRows.getRemainGroups()) {
            lackDetails.add(ErrorDetail.lackSrc(remainGroup));
        }
        return new CompareResult(passCount, lackDetails, mismatchDetails);
    }

    public void checkExpression(CompareData compareData, String checkExpression) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(COMPARE_DATA, compareData);
        try {
            String result = StringUtils.trim(VelocityUtil.getString(checkExpression, params));
            if (StringUtils.isNotEmpty(result)) {
                throw new ErrorException(TaskErrorCodeEnum.CHECK_EXPRESSION_ERROR, result);
            }
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

}
