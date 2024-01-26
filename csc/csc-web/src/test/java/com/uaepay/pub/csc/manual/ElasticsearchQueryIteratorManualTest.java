package com.uaepay.pub.csc.manual;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.uaepay.pub.csc.cases.util.FileUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIteratorFactory;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.base.TestConstants;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;

@Disabled
public class ElasticsearchQueryIteratorManualTest extends MockTestBase {

    @Autowired
    QueryDataIteratorFactory queryDataIteratorFactory;

    QueryRows queryRows;

    @Test
    public void test() {
        String template = FileUtil.readToString("es_manual_test/script_1_log_cgs_success_ratio.json");
        QueryParam queryParam = new QueryParam().beginTime(new DateTime(2022, 10, 13, 0, 0, 0).toDate())
            .endTime(new DateTime(2022, 10, 14, 0, 0, 0).toDate());
        MonitorDefine define =
            new MonitorDefineBuilder("test").es(TestConstants.DATASOURCE_ES_LOG).alarm(template).noSplit().build();
        QueryDataIterator queryDataIterator = queryDataIteratorFactory.create(define, queryParam);

        // 分页1
        Assertions.assertTrue(queryDataIterator.hasNext());
        queryRows = queryDataIterator.next();
        queryRows.getRows().forEach(System.out::println);
    }

}
