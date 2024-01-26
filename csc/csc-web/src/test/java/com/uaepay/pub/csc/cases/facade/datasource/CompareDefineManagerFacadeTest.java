package com.uaepay.pub.csc.cases.facade.datasource;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.basis.beacon.service.facade.domain.response.ObjectQueryResponse;
import com.uaepay.basis.beacon.service.facade.enums.common.ApplyStatusEnum;
import com.uaepay.pub.csc.service.facade.CompareDefineManagerFacade;
import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;
import com.uaepay.pub.csc.test.base.MockTestBase;

public class CompareDefineManagerFacadeTest extends MockTestBase {

    @Autowired
    CompareDefineManagerFacade compareDefineManagerFacade;

    @Test
    public void testGetDataSourceCodeList() {
        ObjectQueryResponse<List<String>> response = compareDefineManagerFacade.getDataSourceCodeList(operateRequest());
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Assertions.assertTrue(response.getResult().contains("default"));
        Assertions.assertTrue(response.getResult().contains("unittest_error_noauth"));
        Assertions.assertTrue(response.getResult().contains("unittest_error_nodb"));
        Assertions.assertTrue(response.getResult().contains("mongo_reader"));
    }

    @Test
    public void testGetDataSourceMap() {
        ObjectQueryResponse<Map<String, List<String>>> response =
            compareDefineManagerFacade.getDataSourceMap(operateRequest());
        Assertions.assertEquals(ApplyStatusEnum.SUCCESS, response.getApplyStatus());
        Map<String, List<String>> result = response.getResult();
        Assertions.assertEquals(4, result.size());

        List<String> mysqlList = result.get(DataSourceTypeEnum.MYSQL.getCode());
        Assertions.assertEquals(3, mysqlList.size());
        Assertions.assertTrue(mysqlList.contains("default"));
        Assertions.assertTrue(mysqlList.contains("unittest_error_noauth"));
        Assertions.assertTrue(mysqlList.contains("unittest_error_nodb"));

        List<String> mongoList = result.get(DataSourceTypeEnum.MONGO.getCode());
        Assertions.assertEquals(2, mongoList.size());
        Assertions.assertTrue(mongoList.contains("mongo_reader"));
        Assertions.assertTrue(mongoList.contains("mongo2_reader"));

        List<String> esList = result.get(DataSourceTypeEnum.ES.getCode());
        Assertions.assertEquals(2, esList.size());
        Assertions.assertTrue(esList.contains("es_business_reader"));
        Assertions.assertTrue(esList.contains("es_log_reader"));

        List<String> apiList = result.get(DataSourceTypeEnum.API.getCode());
        Assertions.assertEquals(1, apiList.size());
        Assertions.assertTrue(apiList.contains(DataSourceTypeEnum.API.getCode()));
    }

}
