package com.uaepay.pub.csc.test.builder;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.test.base.TestConstants;

public class CompareDefineBuilder {

    public CompareDefineBuilder(String defineName) {
        result.setDefineName(defineName);
        result.setEnableFlag(YesNoEnum.YES);
        result.setMemo("unit-test");
    }

    private CompareDefine result = new CompareDefine();

    public CompareDefineBuilder src(String relateField, int splitMinutes, String template) {
        return src(TestConstants.DATASOURCE_CODE_DEFAULT, relateField, splitMinutes, template);
    }

    public CompareDefineBuilder src(String datasourceCode, String relateField, int splitMinutes, String template) {
        result.setSrcDatasourceCode(datasourceCode);
        result.setSrcRelateField(relateField);
        result.setSrcSplitMinutes(splitMinutes);
        result.setSrcSqlTemplate(template);
        return this;
    }

    public CompareDefineBuilder target(String relateField, String template) {
        return target(TestConstants.DATASOURCE_CODE_DEFAULT, relateField, template);
    }

    public CompareDefineBuilder target(String datasourceCode, String relateField, String template) {
        result.setTargetDatasourceCode(datasourceCode);
        result.setTargetRelateField(relateField);
        result.setTargetSqlTemplate(template);
        result.setTargetShardingExpression(null);
        return this;
    }

    public CompareDefineBuilder srcDatasourceCode(String srcDatasourceCode) {
        result.setSrcDatasourceCode(srcDatasourceCode);
        return this;
    }

    public CompareDefineBuilder compare(String checkExpression) {
        result.setCheckExpression(checkExpression);
        return this;
    }

    public CompareDefineBuilder compensate(String compensateExpression, String notifyConfig) {
        result.setCompensateExpression(compensateExpression);
        result.setCompensateConfig(notifyConfig);
        return this;
    }

    public CompareDefine build() {
        return result;
    }

}
