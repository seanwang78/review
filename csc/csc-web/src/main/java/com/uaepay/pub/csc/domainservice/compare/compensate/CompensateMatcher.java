package com.uaepay.pub.csc.domainservice.compare.compensate;

/**
 * 补单匹配工具
 * 
 * @author zc
 */
public interface CompensateMatcher {

    /**
     * 比对少数据，则补单
     */
    void lack();

    /**
     * 比对少数据，且匹配源数据字段和值，则补单
     *
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param srcValues
     *            源数据值列表，大小写敏感，逗号分隔
     * @param notifyStatus
     *            通知状态
     */
    void lack(String srcField, String srcValues, String... notifyStatus);

    /**
     * 比对不一致，且匹配目标数据的字段、值，则补单
     *
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param srcValues
     *            源数据值列表，大小写敏感，逗号分隔
     * @param targetField
     *            目标数据字段名称，不区分大小写
     * @param targetValues
     *            目标数据值列表，大小写敏感，逗号分隔
     * @param notifyStatus
     *            通知状态
     */
    void mismatch(String srcField, String targetField, String srcValues, String targetValues, String... notifyStatus);

    /**
     * 比对不一致，且匹配不到目标数据的字段、值，则补单
     * 
     * @param srcField
     *            源数据字段名称，不区分大小写
     * @param srcValues
     *            源数据值列表，大小写敏感，逗号分隔
     * @param targetField
     *            目标数据字段名称，不区分大小写
     * @param targetValue
     *            目标数据值，大小写敏感
     * @param notifyStatus
     *            通知状态
     */
    void mismatchMiss(String srcField, String targetField, String srcValues, String targetValue,
        String... notifyStatus);

}
