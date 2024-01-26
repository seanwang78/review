package com.uaepay.pub.csc.test.base;

import com.uaepay.pub.csc.service.facade.enums.DataSourceTypeEnum;

public interface TestConstants {

    String WHATEVER = "whatever";

    String DATASOURCE_CODE_DEFAULT = "default";

    String DATASOURCE_CODE_NOAUTH = "unittest_error_noauth";

    String MONITOR_REPORT_STATUS = "ut_mr_status";

    String MONITOR_REPORT_STATUS_SQL = "select DATE_FORMAT(t.update_time, '%Y-%m-%d') UpdateDate"
        + " , sum(case when t.status = 'S' then t.amount else 0 end) SuccessAmount, sum(case when t.status = 'S' then 1 else 0 end) SuccessCount"
        + " , sum(case when t.status = 'F' then 1 else 0 end) FailCount from csc.t_test_data t where data_set = '"
        + MONITOR_REPORT_STATUS + "' and update_time >= {begin} and update_time < {end}"
        + " group by DATE_FORMAT(t.update_time, '%Y-%m-%d') order by DATE_FORMAT(t.update_time, '%Y-%m-%d')"
        + " limit {count}";

    String MONITOR_ALARM_STATUS = "ut_ma_status";

    String MONITOR_ALARM_STATUS_SQL =
        "select t.order_no, t.status, t.amount, DATE_FORMAT(t.update_time, '%Y-%m-%d %H:%i:%s') update_time from csc.t_test_data t "
            + " where data_set = '" + MONITOR_ALARM_STATUS
            + "' and t.update_time >= {begin} and t.update_time < {end} and t.status = 'E' limit {count}";

    String MONITOR_ALARM_STATUS_MONGO_FIND = "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: '"
        + MONITOR_ALARM_STATUS + "'}, {status: 'E'}" + ", {updateTime: {$gte: {begin}, $lt: {end}}}"
        + "]}, projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1}}";

    String MONITOR_ALARM_STATUS_MONGO_FIND_DIFFERENT_FIELDS =
        "{db: 'testdb', find: 'csc_test_data', filter: {$and: [{dataSet: '" + MONITOR_ALARM_STATUS + "'}, {status: 'E'}"
            + ", {updateTime: {$gte: {begin}, $lt: {end}}}"
            + "]}, projection: {_id: 0, orderNo: 1, status: 1, amount: 1, updateTime: 1, currency: 1, group1: 1}}";

    String MONITOR_ALARM_STATUS_ES_FIND = "{\"index\": \"i_dev_csc_test_data\""
        + ", \"query\": { \"bool\": { \"filter\": [ {\"match\": {\"dataSet\": \"" + MONITOR_ALARM_STATUS + "\"}}"
        + ", {\"match\": {\"status\": \"E\"}}"
        + ", {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}} ]}}"
        + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"], \"sort\": {\"updateTime\": \"asc\"}}";

    String MONITOR_ALARM_ES_AGGREGATION = "{\"index\": \"i_dev_csc_test_data\""
        + ", \"query\": { \"bool\": { \"filter\": [ {\"match\": {\"dataSet\": \"" + MONITOR_ALARM_STATUS + "\"}}"
        + ", {\"match\": {\"status\": \"E\"}}"
        + ", {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}} ]}}"
        + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"], \"sort\": {\"updateTime\": \"asc\"}"
        + ",  \"aggregations\": {\n" + "    \"dataType\": {\n" + "      \"terms\": {\n"
        + "        \"field\": \"dataType.keyword\",\n" + "        \"size\": 10\n" + "      },\n" + "      \"aggs\": {\n"
        + "        \"dataSet\": {\n" + "          \"terms\": {\n" + "            \"field\": \"dataSet.keyword\",\n"
        + "            \"size\": 10\n" + "          },\n" + "          \"aggs\": {\n" + "            \"max\": {\n"
        + "              \"max\": {\n" + "                \"field\": \"updateTime\"\n" + "              }\n"
        + "            },\n" + "            \"min\": {\n" + "              \"min\": {\n"
        + "                \"field\": \"amount\"\n" + "              }\n" + "            },\n"
        + "            \"avg\": {\n" + "              \"avg\": {\n" + "                \"field\": \"amount\"\n"
        + "              }\n" + "            },\n" + "            \"sum\": {\n" + "              \"sum\": {\n"
        + "                \"field\": \"amount\"\n" + "              }\n" + "            }\n" + "          }\n"
        + "        }\n" + "      }\n" + "    }\n" + "  }\n" + "}";

    String MONITOR_ALARM_ES_AGGREGATION_BADSQL_1 = "{\"index\": \"i_dev_csc_test_data\""
        + ", \"query\": { \"bool\": { \"filter\": [ {\"match\": {\"dataSet\": \"" + MONITOR_ALARM_STATUS + "\"}}"
        + ", {\"match\": {\"status\": \"E\"}}"
        + ", {\"range\": {\"updateTime\": {\"gte\": {begin}, \"lt\": {end}}}} ]}}"
        + ", \"_source\": [\"orderNo\", \"status\", \"amount\", \"updateTime\"], \"sort\": {\"updateTime\": \"asc\"}"
        + ",  \"aggregations\": {\n" + " \"orderNo\":{\n" + "      \"terms\": {\n"
        + "        \"field\": \"orderNo.keyword\",\n" + "        \"size\": 10\n" + "      }\n" + "      \n" + "    },"
        + "    \"dataType\": {\n" + "      \"terms\": {\n" + "        \"field\": \"dataType.keyword\",\n"
        + "        \"size\": 10\n" + "      },\n" + "      \"aggs\": {\n" + "        \"dataSet\": {\n"
        + "          \"terms\": {\n" + "            \"field\": \"dataSet.keyword\",\n" + "            \"size\": 10\n"
        + "          },\n" + "          \"aggs\": {\n" + "            \"max\": {\n" + "              \"max\": {\n"
        + "                \"field\": \"updateTime\"\n" + "              }\n" + "            },\n"
        + "            \"min\": {\n" + "              \"min\": {\n" + "                \"field\": \"amount\"\n"
        + "              }\n" + "            },\n" + "            \"avg\": {\n" + "              \"avg\": {\n"
        + "                \"field\": \"amount\"\n" + "              }\n" + "            },\n"
        + "            \"sum\": {\n" + "              \"sum\": {\n" + "                \"field\": \"amount\"\n"
        + "              }\n" + "            }\n" + "          }\n" + "        }\n" + "      }\n" + "    }\n" + "  }\n"
        + "}";

    String DATASOURCE_MONGO = "mongo_reader";

    String DATASOURCE_MONGO_2 = "mongo2_reader";

    String DATASOURCE_ES = "es_business_reader";
    String DATASOURCE_ES_LOG = "es_log_reader";

    String DATASOURCE_API = DataSourceTypeEnum.API.getCode();

}
