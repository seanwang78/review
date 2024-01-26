package com.uaepay.pub.csc.core.dal.dataobject.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.uaepay.basis.beacon.common.util.BooleanUtil;
import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.domain.enums.AttachType;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.PriorityEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;

/**
 * 监控定义
 * 
 * @author zc
 */
public class MonitorDefine {

    public static final String DETAIL = "detail";
    public static final String TEAMS = "teams";

    /** 是否增加csv的附件 */
    public static final String ATTACH = "attach";

    /**
     * 定义ID
     */
    private Long defineId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 定义名称
     */
    private String defineName;

    /**
     * 数据源类型：MYSQL，ES
     * 
     * @see DataSourceTypeEnum
     */
    private String datasourceType;

    /**
     * 数据源代码
     */
    private String datasourceCode;

    /**
     * 监控类型：R=报表，A=报警
     */
    private MonitorTypeEnum monitorType;

    /**
     * 优先级：H=高，M=中，L=低
     */
    private PriorityEnum priority;

    /**
     * 切分策略：NO=不切分，UNION=拼接，SUM=汇总
     */
    private SplitStrategyEnum splitStrategy;

    /**
     * 数据切分时长
     */
    private Integer splitMinutes;

    /**
     * 关键字段
     */
    private String keyField;

    /**
     * 通知表达式
     */
    private String notifyExpression;

    /**
     * 启用标识：Y=启用，N=停用
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

    /**
     * 查询模版
     */
    private String queryTemplate;

    /**
     * 是否记录明细
     * 
     * @return
     */
    public boolean isRecordDetail() {
        return extension != null && BooleanUtil.parse(extension.get(DETAIL));
    }

    public void setRecordDetail(boolean flag) {
        if (extension == null) {
            extension = new HashMap<>();
        }
        extension.put(DETAIL, flag ? YesNoEnum.YES.getCode() : YesNoEnum.NO.getCode());
    }

    public AttachType getAttachType() {
        if (extension == null) {
            return null;
        }
        return AttachType.getByCode(this.extension.get(ATTACH));
    }

    /**
     * 是否通知teams
     *
     * @return
     */
    public boolean isNotifyTeams() {
        return extension != null && BooleanUtil.parse(extension.get(TEAMS));
    }

    public void setNotifyTeams(boolean flag) {
        if (extension == null) {
            extension = new HashMap<>();
        }
        extension.put(TEAMS, flag ? YesNoEnum.YES.getCode() : YesNoEnum.NO.getCode());
    }

    public Long getDefineId() {
        return defineId;
    }

    public void setDefineId(Long defineId) {
        this.defineId = defineId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getDefineName() {
        return defineName;
    }

    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    public void setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
    }

    public Integer getSplitMinutes() {
        return splitMinutes;
    }

    public void setSplitMinutes(Integer splitMinutes) {
        this.splitMinutes = splitMinutes;
    }

    public String getKeyField() {
        return keyField;
    }

    public void setKeyField(String keyField) {
        this.keyField = keyField;
    }

    public String getNotifyExpression() {
        return notifyExpression;
    }

    public void setNotifyExpression(String notifyExpression) {
        this.notifyExpression = notifyExpression;
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

    public String getQueryTemplate() {
        return queryTemplate;
    }

    public void setQueryTemplate(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }

    public MonitorTypeEnum getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(MonitorTypeEnum monitorType) {
        this.monitorType = monitorType;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
    }

    public SplitStrategyEnum getSplitStrategy() {
        return splitStrategy;
    }

    public void setSplitStrategy(SplitStrategyEnum splitStrategy) {
        this.splitStrategy = splitStrategy;
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
}