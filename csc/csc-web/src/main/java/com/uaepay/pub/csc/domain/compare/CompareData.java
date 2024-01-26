package com.uaepay.pub.csc.domain.compare;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;

import com.uaepay.pub.csc.core.common.util.CscUtil;
import com.uaepay.pub.csc.domain.data.RowData;

import lombok.Data;

/**
 * 比对数据
 * 
 * @author zc
 */
@Data
public class CompareData {

    public CompareData(GroupRows srcGroup, GroupRows targetGroup) {
        this.srcGroup = srcGroup;
        this.targetGroup = targetGroup;
        if (srcGroup == null || srcGroup.isEmpty() || targetGroup == null || targetGroup.isEmpty()) {
            throw new IllegalArgumentException("compare data can't be empty");
        }
    }

    GroupRows srcGroup;

    GroupRows targetGroup;

    Set<String> errorMessages = new LinkedHashSet<>();

    /**
     * 判断值是否对应
     *
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param srcValues
     *            源数据值列表，大小写敏感，逗号分隔
     * @param targetField
     *            目标数据字段名称，不区分大小写
     * @param targetValues
     *            目标数据值列表，大小写敏感，逗号分隔
     */
    public void isCorrespond(String srcField, String targetField, String srcValues, String targetValues) {
        if (!checkFieldExist(srcField, targetField)) {
            return;
        }
        List<String> srcValueList = CscUtil.splitString(srcValues);
        List<String> targetValueList = CscUtil.splitString(targetValues);
        for (RowData srcData : srcGroup.getRows()) {
            String srcValue = srcData.getItem(StringUtils.lowerCase(srcField)) + "";
            // 源数据值不在范围内
            if (!srcValueList.contains(srcValue)) {
                continue;
            }
            for (RowData targetData : targetGroup.getRows()) {
                String targetValue = targetData.getItem(StringUtils.lowerCase(targetField)) + "";
                // 源数据值在范围内，校验目标数据值
                if (targetValueList.contains(targetValue)) {
                    continue;
                }
                errorMessages.add(MessageFormat.format("{0}[{1}] and {2}[{3}] not correspond, rule: [{4}] -> [{5}]",
                    srcField, srcValue, targetField, targetValue, srcValues, targetValues));
            }
        }
    }

    /**
     * 判断值是否全部对应上
     *
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param srcValues
     *            源数据值列表，大小写敏感，逗号分隔
     * @param targetField
     *            目标数据字段名称，不区分大小写
     * @param targetValues
     *            目标数据值列表，大小写敏感，逗号分隔
     */
    public void isCorrespondAll(String srcField, String targetField, String srcValues, String targetValues) {
        if (!checkFieldExist(srcField, targetField)) {
            return;
        }
        List<String> srcValueList = CscUtil.splitString(srcValues);
        for (RowData srcData : srcGroup.getRows()) {
            String srcValue = srcData.getItem(StringUtils.lowerCase(srcField)) + "";
            // 源数据值不在范围内
            if (!srcValueList.contains(srcValue)) {
                continue;
            }
            List<String> targetValueRemains = CscUtil.splitString(targetValues);
            for (RowData targetData : targetGroup.getRows()) {
                String targetValue = targetData.getItem(StringUtils.lowerCase(targetField)) + "";
                // 源数据值在范围内，校验目标数据值
                if (targetValueRemains.contains(targetValue)) {
                    targetValueRemains.remove(targetValue);
                    continue;
                }
            }
            if (targetValueRemains.size() != 0) {
                errorMessages
                    .add(MessageFormat.format("{0}[{1}] and {2}[remain {3}] not correspond all, rule: [{4}] -> [{5}]",
                        srcField, srcValue, targetField, targetValueRemains, srcValues, targetValues));
            }
        }
    }

    /**
     * 判断值是否相等
     * 
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param targetField
     *            目标数据字段名称，不区分大小写
     */
    public void isEqual(String srcField, String targetField) {
        if (!checkFieldExist(srcField, targetField)) {
            return;
        }
        for (RowData srcData : srcGroup.getRows()) {
            Object srcValue = srcData.getItem(StringUtils.lowerCase(srcField));
            for (RowData targetData : targetGroup.getRows()) {
                Object targetValue = targetData.getItem(StringUtils.lowerCase(targetField));
                if (checkValueEquals(srcValue, targetValue)) {
                    continue;
                }
                errorMessages.add(MessageFormat.format("{0}[{1}] and {2}[{3}] not equal", srcField, srcValue,
                    targetField, targetValue));
            }
        }
    }

    /**
     * 判断源数据字段值在列表中
     *
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param srcValues
     *            源数据值列表，大小写敏感，逗号分隔
     */
    public void isIn(String srcField, String srcValues) {
        if (!checkFieldExist(srcField)) {
            return;
        }
        List<String> srcValueList = CscUtil.splitString(srcValues);
        for (RowData srcData : srcGroup.getRows()) {
            String srcValue = srcData.getItem(StringUtils.lowerCase(srcField)) + "";
            // 源数据值不在范围内
            if (!srcValueList.contains(srcValue)) {
                errorMessages.add(MessageFormat.format("{0}[{1}] not in list[{2}]", srcField, srcValue, srcValues));
            }
        }
    }

    public static boolean checkValueEquals(Object srcValue, Object targetValue) {
        srcValue = convertToUnifiedType(srcValue);
        targetValue = convertToUnifiedType(targetValue);

        srcValue = convertToUnifiedTypeWithCompareValue(srcValue, targetValue);
        targetValue = convertToUnifiedTypeWithCompareValue(targetValue, srcValue);

        return Objects.equals(srcValue, targetValue);
    }

    /** 转化为统一的类型，数字统一转化为BigDecimal */
    private static Object convertToUnifiedType(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return new BigDecimal((Integer)value);
        } else if (value instanceof Long) {
            return new BigDecimal((Long)value);
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger)value);
        }
        return value;
    }

    /** 转化为统一类型，依据比较值 */
    private static Object convertToUnifiedTypeWithCompareValue(Object value, Object compareValue) {
        if (value == null) {
            return null;
        }
        if (value instanceof String && compareValue instanceof BigDecimal) {
            value = tryConvert(value, v -> new BigDecimal((String)v));
        }
        if (value instanceof BigDecimal && compareValue instanceof BigDecimal) {
            BigDecimal valueDecimal = (BigDecimal)value;
            BigDecimal compareDecimal = (BigDecimal)compareValue;
            if (compareDecimal.scale() > valueDecimal.scale()) {
                value = valueDecimal.setScale(compareDecimal.scale(), RoundingMode.UNNECESSARY);
            }
        }
        return value;
    }

    private static Object tryConvert(Object value, Function<Object, Object> function) {
        try {
            return function.apply(value);
        } catch (Throwable e) {
            return value;
        }
    }

    private boolean checkFieldExist(String srcField, String targetField) {
        for (RowData srcData : srcGroup.getRows()) {
            if (!srcData.getColumnData().containsColumn(srcField)) {
                errorMessages.add(MessageFormat.format("source field [{0}] not exist", srcField));
                return false;
            }
        }
        for (RowData targetData : targetGroup.getRows()) {
            if (!targetData.getColumnData().containsColumn(targetField)) {
                errorMessages.add(MessageFormat.format("target field [{0}] not exist", targetField));
                return false;
            }
        }
        return true;
    }

    private boolean checkFieldExist(String srcField) {
        for (RowData srcData : srcGroup.getRows()) {
            if (!srcData.getColumnData().containsColumn(srcField)) {
                errorMessages.add(MessageFormat.format("source field [{0}] not exist", srcField));
                return false;
            }
        }
        return true;
    }

}
