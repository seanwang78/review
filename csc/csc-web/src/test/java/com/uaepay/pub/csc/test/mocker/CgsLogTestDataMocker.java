package com.uaepay.pub.csc.test.mocker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.domain.data.DataSourceConfig;
import com.uaepay.pub.csc.domain.data.EsDataSourceConfig;
import com.uaepay.pub.csc.domainservice.data.DataSourceConfigFactory;
import com.uaepay.pub.csc.domainservice.data.event.DataSourceRegisteredEvent;
import com.uaepay.pub.csc.test.builder.CgsLogTestDataBuilder;
import com.uaepay.pub.csc.test.dal.es.CgsLogTestDataEsMapper;
import com.uaepay.pub.csc.test.domain.CgsLogTestData;

@Service
public class CgsLogTestDataMocker implements ApplicationListener<DataSourceRegisteredEvent> {

    @Autowired
    CgsLogTestDataEsMapper cgsLogTestDataEsMapper;

    RestHighLevelClient client;

    DateTime beginTime, endTime;
    List<CgsLogTestDataBuilder> builders = new ArrayList<>();

    public CgsLogTestDataMocker reset(DateTime beginTime, DateTime endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.builders.clear();
        return this;
    }

    public CgsLogTestDataBuilder builder(String appCode, String apiCode, String returnCode, String returnMsg) {
        CgsLogTestDataBuilder builder = new CgsLogTestDataBuilder(appCode, apiCode, returnCode, returnMsg);
        builders.add(builder);
        return builder;
    }

    public void mockEs() {
        Set<String> shardingList = new HashSet<>();
        for (CgsLogTestDataBuilder builder : builders) {
            String sharding = builder.getSharding();
            if (StringUtils.isNotBlank(sharding)) {
                shardingList.add(sharding);
            }
        }
        // 删除数据
        for (String sharding : shardingList) {
            long delete = cgsLogTestDataEsMapper.delete(beginTime.toDate(), endTime.toDate(), sharding);
            if (delete != 0) {
                System.out.printf("删除es测试数据: %s ~ %s, %s, count=%d\n", beginTime, endTime, sharding, delete);
            }
        }
        // 保存数据
        for (CgsLogTestDataBuilder builder : builders) {
            CgsLogTestData testData = builder.build();
            String sharding = builder.getSharding();
            System.out.printf("mock数据: %s, %s\n", testData, sharding);
            cgsLogTestDataEsMapper.insert(testData, sharding);
        }
        try {
            // 等索引延迟
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationEvent(DataSourceRegisteredEvent event) {
        DataSourceConfigFactory dataSourceConfigFactory = event.getDataSourceConfigFactory();
        DataSourceConfig dataSourceConfig = dataSourceConfigFactory.getOrCreate("es_log_reader");
        if (dataSourceConfig instanceof EsDataSourceConfig) {
            client = ((EsDataSourceConfig)dataSourceConfig).getRestHighLevelClient();
        }
    }
}
