package com.uaepay.pub.csc.test.builder;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.enums.AttachType;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.PriorityEnum;
import com.uaepay.pub.csc.service.facade.enums.SplitStrategyEnum;
import com.uaepay.pub.csc.test.base.TestConstants;

import java.util.HashMap;
import java.util.Map;

public class MonitorDefineBuilder {

    public MonitorDefineBuilder(String defineName) {
        result.setDefineName(defineName);
        result.setEnableFlag(YesNoEnum.YES);
        result.setMemo("unit-test");

        result.setSplitMinutes(0);
        result.setSplitStrategy(SplitStrategyEnum.NO);

        result.setApplicationName("csc-unit-test");
        result.setDatasourceType(DataSourceTypeEnum.MYSQL.getCode());
        result.setDatasourceCode(TestConstants.DATASOURCE_CODE_DEFAULT);
        result.setPriority(PriorityEnum.LOW);
    }

    private MonitorDefine result = new MonitorDefine();

    /**
     * 可选，默认mysql的csc数据源
     * 
     * @param datasourceType
     *            数据源类型
     * @param datasourceCode
     *            数据源代码
     */
    public MonitorDefineBuilder datasource(DataSourceTypeEnum datasourceType, String datasourceCode) {
        result.setDatasourceType(datasourceType.getCode());
        result.setDatasourceCode(datasourceCode);
        return this;
    }

    /**
     * 必须，报表相关参数
     * 
     * @param queryTemplate
     *            查询模版
     */
    public MonitorDefineBuilder report(String queryTemplate) {
        result.setMonitorType(MonitorTypeEnum.REPORT);
        result.setQueryTemplate(queryTemplate);
        return this;
    }

    public MonitorDefineBuilder alarm(String queryTemplate) {
        return alarm(queryTemplate, null, null);
    }

    public MonitorDefineBuilder alarm(String queryTemplate, String keyField) {
        return alarm(queryTemplate, keyField, null);
    }

    /**
     * 必须，报警相关参数
     * 
     * @param queryTemplate
     *            查询模版
     * @param keyField
     *            关键字段
     * @param notifyExpression
     *            通知级别表达式
     */
    public MonitorDefineBuilder alarm(String queryTemplate, String keyField, String notifyExpression) {
        result.setMonitorType(MonitorTypeEnum.ALARM);
        result.setQueryTemplate(queryTemplate);
        result.setKeyField(keyField);
        result.setNotifyExpression(notifyExpression);
        return this;
    }

    public MonitorDefineBuilder mongo() {
        result.setDatasourceType(DataSourceTypeEnum.MONGO.getCode());
        result.setDatasourceCode(TestConstants.DATASOURCE_MONGO);
        return this;
    }

    public MonitorDefineBuilder mongo2() {
        result.setDatasourceType(DataSourceTypeEnum.MONGO.getCode());
        result.setDatasourceCode(TestConstants.DATASOURCE_MONGO_2);
        return this;
    }

    public MonitorDefineBuilder es() {
        return es(TestConstants.DATASOURCE_ES);
    }

    public MonitorDefineBuilder es(String datasourceCode) {
        result.setDatasourceType(DataSourceTypeEnum.ES.getCode());
        result.setDatasourceCode(datasourceCode);
        return this;
    }

    public MonitorDefineBuilder api() {
        result.setDatasourceType(DataSourceTypeEnum.API.getCode());
        result.setDatasourceCode(DataSourceTypeEnum.API.getCode());
        return this;
    }

    public MonitorDefineBuilder recordDetail() {
        result.setRecordDetail(true);
        return this;
    }

    public MonitorDefineBuilder notifyTeams() {
        result.setNotifyTeams(true);
        return this;
    }

    public MonitorDefineBuilder attach(AttachType attachType) {
        Map<String, String> extension = result.getExtension();
        if (extension == null) {
            extension = new HashMap<>();
        }
        extension.put(MonitorDefine.ATTACH, attachType.getCode());
        result.setExtension(extension);
        return this;
    }

    public MonitorDefineBuilder noSplit() {
        return split(SplitStrategyEnum.NO, 0);
    }

    /**
     * 可选，切分及合并策略
     * 
     * @param splitStrategy
     *            切分策略
     * @param splitMinutes
     *            切分分钟数
     */
    public MonitorDefineBuilder split(SplitStrategyEnum splitStrategy, int splitMinutes) {
        result.setSplitStrategy(splitStrategy);
        result.setSplitMinutes(splitMinutes);
        return this;
    }

    public MonitorDefine build() {
        return result;
    }

}
