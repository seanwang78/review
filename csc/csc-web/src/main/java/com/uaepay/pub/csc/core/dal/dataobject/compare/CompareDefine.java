package com.uaepay.pub.csc.core.dal.dataobject.compare;

import java.util.Date;
import java.util.Map;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;

public class CompareDefine {
    /**
     * 定义ID
     */
    private Long defineId;

    /**
     * 定义名称
     */
    private String defineName;

    /**
     * 源数据源代码
     */
    private String srcDatasourceCode;

    /**
     * 源数据查询模版
     */
    private String srcSqlTemplate;

    /**
     * 源数据切分时长
     */
    private Integer srcSplitMinutes;

    /**
     * 源数据关联字段
     */
    private String srcRelateField;

    /**
     * 目标数据源代码
     */
    private String targetDatasourceCode;

    /**
     * 目标数据查询模版
     */
    private String targetSqlTemplate;

    /**
     * 目标数据关联字段
     */
    private String targetRelateField;

    /**
     * 目标数据分片表达式
     */
    private String targetShardingExpression;

    /**
     * 校验表达式
     */
    private String checkExpression;

    /**
     * 补单表达式
     */
    private String compensateExpression;

    /**
     * 补单配置
     */
    private String compensateConfig;

    /**
     * 启用标志：Y=启用，N=停用
     */
    private YesNoEnum enableFlag;

    /**
     * 扩展参数
     */
    private Map<String, String> extension;

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

    public Long getDefineId() {
        return defineId;
    }

    public void setDefineId(Long defineId) {
        this.defineId = defineId;
    }

    public String getDefineName() {
        return defineName;
    }

    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }

    public String getSrcDatasourceCode() {
        return srcDatasourceCode;
    }

    public void setSrcDatasourceCode(String srcDatasourceCode) {
        this.srcDatasourceCode = srcDatasourceCode;
    }

    public String getSrcSqlTemplate() {
        return srcSqlTemplate;
    }

    public void setSrcSqlTemplate(String srcSqlTemplate) {
        this.srcSqlTemplate = srcSqlTemplate;
    }

    public Integer getSrcSplitMinutes() {
        return srcSplitMinutes;
    }

    public void setSrcSplitMinutes(Integer srcSplitMinutes) {
        this.srcSplitMinutes = srcSplitMinutes;
    }

    public String getSrcRelateField() {
        return srcRelateField;
    }

    public void setSrcRelateField(String srcRelateField) {
        this.srcRelateField = srcRelateField;
    }

    public String getTargetDatasourceCode() {
        return targetDatasourceCode;
    }

    public void setTargetDatasourceCode(String targetDatasourceCode) {
        this.targetDatasourceCode = targetDatasourceCode;
    }

    public String getTargetSqlTemplate() {
        return targetSqlTemplate;
    }

    public void setTargetSqlTemplate(String targetSqlTemplate) {
        this.targetSqlTemplate = targetSqlTemplate;
    }

    public String getTargetRelateField() {
        return targetRelateField;
    }

    public void setTargetRelateField(String targetRelateField) {
        this.targetRelateField = targetRelateField;
    }

    public String getTargetShardingExpression() {
        return targetShardingExpression;
    }

    public void setTargetShardingExpression(String targetShardingExpression) {
        this.targetShardingExpression = targetShardingExpression;
    }

    public String getCheckExpression() {
        return checkExpression;
    }

    public void setCheckExpression(String checkExpression) {
        this.checkExpression = checkExpression;
    }

    public String getCompensateExpression() {
        return compensateExpression;
    }

    public void setCompensateExpression(String compensateExpression) {
        this.compensateExpression = compensateExpression;
    }

    public String getCompensateConfig() {
        return compensateConfig;
    }

    public void setCompensateConfig(String compensateConfig) {
        this.compensateConfig = compensateConfig;
    }

    public YesNoEnum getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(YesNoEnum enableFlag) {
        this.enableFlag = enableFlag;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
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
}