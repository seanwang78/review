package com.uaepay.pub.csc.core.dal.dataobject.logmonitor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class LogRule {
    /**
     * ID
     */
    private Integer id;

    /**
     * 功能: porter_cgs,porter_sgs
     * com.uaepay.pub.csc.service.facade.enums.LogRuleFunctionCodeEnum
     */
    private String functionCode;

    /**
     * 规则类型: 黑名单=BLACK,白名单=WHITE
     * com.uaepay.pub.csc.service.facade.enums.LogRuleTypeEnum
     */
    private String ruleType;


    /**
     * 系统编码
     */
    private String appCode;

    /**
     * api
     */
    private String apiCode;

    /**
     * 返回码
     */
    private String returnCode;

    /**
     * 表达式类型: 正则=REGEX，全文匹配=TEXT,空白=NONE
     * com.uaepay.pub.csc.service.facade.enums.LogRuleExpressionTypeEnum
     */
    private String expressionType;

    /**
     * 匹配表达式: 返回消息匹配表达式
     */
    private String matchExpression;

    /**
     * 启用标识: Y=启用，N=停用
     */
    private String enableFlag;

    /**
     * 最后修改人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String memo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String getMatchExpression() {
        return matchExpression;
    }

    public void setMatchExpression(String matchExpression) {
        this.matchExpression = matchExpression;
    }

    public String getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(String enableFlag) {
        this.enableFlag = enableFlag;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}