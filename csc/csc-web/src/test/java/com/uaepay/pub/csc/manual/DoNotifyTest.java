package com.uaepay.pub.csc.manual;

import com.uaepay.pub.csc.CscApplication;
import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyContact;
import com.uaepay.pub.csc.core.dal.dataobject.notify.NotifyGroup;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareDefineMapper;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareTaskMapper;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorDefineMapper;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorTaskMapper;
import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domain.data.ColumnData;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domain.properties.MonitorProperties;
import com.uaepay.pub.csc.domainservice.compare.check.CompareService;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskCallback;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskCallback;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorAction;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorActionFactory;
import com.uaepay.pub.csc.domainservice.notify.NotifyRepository;
import com.uaepay.pub.csc.domainservice.notify.NotifyService;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.DefineTypeEnum;
import com.uaepay.pub.csc.service.facade.enums.NotifyTypeEnum;
import com.uaepay.pub.csc.test.base.ManualTestBase;
import com.uaepay.pub.csc.test.base.MockTestBase;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.builder.NotifyRelationBuilder;
import com.uaepay.pub.csc.test.builder.RowDataBuilder;
import com.uaepay.pub.csc.test.domain.NotifyDefine;
import com.uaepay.pub.csc.test.mocker.MonitorMocker;
import com.uaepay.pub.csc.test.mocker.NotifyRelationMocker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CscApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DoNotifyTest extends ManualTestBase {

    @Value("${env}")
    String environment;

    @Value("${basis.compareDefineUrl}")
    String compareDefineUrl;

    @Value("${basis.compareTaskUrl}")
    String compareTaskUrl;

    @Value("${basis.monitorDefineUrl}")
    String monitorDefineUrl;

    @Value("${basis.monitorTaskUrl}")
    String monitorTaskUrl;

    static final FormatUtil FORMAT_UTIL = new FormatUtil();

    @Autowired
    CompareService compareService;

    @Autowired
    NotifyService notifyService;

    @Autowired
    CompareDefineMapper compareDefineMapper;

    @Autowired
    CompareTaskMapper compareTaskMapper;

    @Autowired
    MonitorDefineMapper monitorDefineMapper;

    @Autowired
    MonitorTaskMapper monitorTaskMapper;

    @Autowired
    MonitorActionFactory monitorActionFactory;

    @Autowired
    QueryDataIteratorFactory queryDataIteratorFactory;

    MonitorProperties monitorProperties;
    private MonitorAction monitorAction;

    private static final String PARAM_DEFINE = "define";
    private static final String PARAM_TASK = "task";
    private static final String PARAM_RESULT = "result";
    private static final String PARAM_ENV = "env";
    private static final String PARAM_FORMAT_UTIL = "format";
    private static final String PARAM_COLUMNS = "columns";
    private static final String PARAM_DETAILS = "details";
    private static final String PARAM_COMPARE_DEFINE_URL = "compareDefineUrl";
    private static final String PARAM_COMPARE_TASK_URL = "compareTaskUrl";
    private static final String PARAM_MONITOR_DEFINE_URL = "monitorDefineUrl";
    private static final String PARAM_MONITOR_TASK_URL = "monitorTaskUrl";

    Logger logger = LoggerFactory.getLogger(getClass());

    private CompareTask compareTask;
    private CompareTaskCallback callback;
    private CompareDefine compareDefine;

    private MonitorTask monitorTask;
    private MonitorTaskCallback monitorTaskCallback;
    private MonitorDefine monitorDefine;

    @Test
    public void test_DoCompareNotify() {

        initCompare();

        SrcRows srcRows = new SrcRows(new RowDataBuilder().columns("order_no", "status", "currency").relate("order_no")
            .row("a", "S", "AED").row("b", "F", "AED").row("c", "F", "AED").row("d", "S", "AED").build());
        TargetRows targetRows = new TargetRows(new RowDataBuilder().columns("order_no", "trade_status", "currency")
            .relate("order_no").row("a", "Fail", "CNY").row("b", "Success", "AED").row("c", "Failure", "AED")
            .row("e", "F", "AED").build());
        String checkExpression = "$data.isCorrespond('status', 'trade_status', 'S', 'Success')\n"
            + "$data.isCorrespond('status', 'trade_status', 'F', 'Fail,Failure')\n"
            + "$data.isEqual('currency', 'currency')";
        CompareResult result = compareService.compare(srcRows, targetRows, checkExpression);
        System.out.println(result);
        notifyService.notifyByTemplate(NotifyTemplateTypeEnum.COMPARE, compareDefine.getDefineId(), null,
            buildCompareNotifyParam(result), true, null);
    }

    @Test
    public void test_DoMonitorNotify() {

        initMonitor();

        // MonitorResult result = executeQuery();
        // System.out.println(result);
        // notifyService.notifyByTemplate(NotifyTemplateTypeEnum.ALARM, monitorDefine.getDefineId(),
        // AlarmLevelEnum.URGENT,
        // buildMonitorNotifyParam(result));

        notifyService.notifyByTemplate(NotifyTemplateTypeEnum.ALARM, monitorDefine.getDefineId(), AlarmLevelEnum.URGENT,
            buildMonitorNotifyParam(), true, null);

    }

    private void initCompare() {
        compareDefine = compareDefineMapper.selectByPrimaryKey(1L);
        compareTask = compareTaskMapper.selectByPrimaryKey(94L);
    }

    private void initMonitor() {
        monitorDefine = monitorDefineMapper.selectByPrimaryKey(473L);
        monitorTask = monitorTaskMapper.selectByPrimaryKey(80L);
    }

    /**
     * 执行查询
     */
    private MonitorResult executeQuery() {
        monitorAction = monitorActionFactory.create(monitorDefine.getMonitorType());
        logger.info("监控执行: define={}, task={}, {}~{}", monitorTask.getDefineId(), monitorTask.getTaskId(),
            DateFormatUtils.ISO_DATETIME_FORMAT.format(monitorTask.getDataBeginTime()),
            DateFormatUtils.ISO_DATETIME_FORMAT.format(monitorTask.getDataEndTime()));
        StopWatch watch = StopWatch.createStarted();
        QueryParam queryParam = new QueryParam(monitorTask.getDataBeginTime(), monitorTask.getDataEndTime());
        QueryDataIterator dataIterator = queryDataIteratorFactory.create(monitorDefine, queryParam);
        int itCount = 0;
        MonitorResult result = new MonitorResult();
        while (dataIterator.hasNext()) {
            QueryRows queryRows = dataIterator.next();
            logger.info("子查询: seq={}, rows={}", ++itCount, queryRows.size());
            if (queryRows.isEmpty()) {
                continue;
            }
            if (!result.merge(queryRows, monitorProperties.getMaxDetailCount(monitorDefine.getMonitorType()),
                monitorDefine.getSplitStrategy())) {
                logger.info("已达到最大明细数，终止执行");
                break;
            }
        }
        monitorAction.afterQuery(monitorDefine, monitorTask, result);
        watch.stop();
        monitorTask.setExecuteConsume((int)watch.getTime(TimeUnit.SECONDS));
        logger.info("结果统计: count={}, isAll={}, consume={}s", result.size(), result.isAll(),
            monitorTask.getExecuteConsume());
        return result;
    }

    private Map<String, Object> buildCompareNotifyParam(CompareResult result) {
        Map<String, Object> params = new HashMap<>(5);
        params.put(PARAM_DEFINE, compareDefine);
        params.put(PARAM_TASK, compareTask);
        params.put(PARAM_ENV, environment);
        params.put(PARAM_COMPARE_DEFINE_URL, String.format(compareDefineUrl, compareTask.getDefineId()));
        params.put(PARAM_COMPARE_TASK_URL, String.format(compareTaskUrl, compareTask.getTaskId()));
        params.put(PARAM_FORMAT_UTIL, FORMAT_UTIL);
        params.put(PARAM_RESULT, result);
        return params;
    }

    private Map<String, Object> buildMonitorNotifyParam() {
        Map<String, Object> params = new HashMap<>(6);
        params.put(PARAM_DEFINE, monitorDefine);
        params.put(PARAM_TASK, monitorTask);
        params.put(PARAM_ENV, environment);
        params.put(PARAM_MONITOR_DEFINE_URL, String.format(monitorDefineUrl, monitorTask.getDefineId()));
        params.put(PARAM_MONITOR_TASK_URL, String.format(monitorTaskUrl, monitorTask.getTaskId()));
        params.put(PARAM_FORMAT_UTIL, FORMAT_UTIL);
        // if (result != null && CollectionUtils.isNotEmpty(result.getDetails())) {
        // params.put(PARAM_COLUMNS, result.getDetails().get(0).getColumnData().getColumnInfos());
        // params.put(PARAM_DETAILS, result.getDetails());
        // }

        ColumnData.Column column0 = new ColumnData.Column(0, "商户号");
        ColumnData.Column column1 = new ColumnData.Column(1, "商户名称");
        ColumnData.Column column2 = new ColumnData.Column(2, "基本户余额");
        ColumnData.Column column3 = new ColumnData.Column(3, "收单户余额");
        ColumnData.Column column4 = new ColumnData.Column(4, "管他什么余额");
        List<ColumnData.Column> colimnInfos = new ArrayList<>();
        colimnInfos.add(column0);
        colimnInfos.add(column1);
        colimnInfos.add(column2);
        colimnInfos.add(column3);
        colimnInfos.add(column4);
        params.put(PARAM_COLUMNS, colimnInfos);

        List<RowData> rowDataList = new ArrayList<>();
        List<Object> objectList = new ArrayList<>();
        objectList.add("200001131600");
        objectList.add("GAME");
        objectList.add(new BigDecimal("267126500020.10"));
        objectList.add(new BigDecimal("267126500020.101"));
        objectList.add(new BigDecimal("2559712"));
        // objectList.add(null);
        RowData rowData0 = new RowData(new ColumnData(colimnInfos, ""), objectList);
        rowDataList.add(rowData0);
        params.put(PARAM_DETAILS, rowDataList);
        return params;
    }

}
