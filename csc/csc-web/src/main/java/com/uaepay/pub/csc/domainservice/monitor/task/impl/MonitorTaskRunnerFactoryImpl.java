package com.uaepay.pub.csc.domainservice.monitor.task.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.uaepay.basis.beacon.service.facade.enums.common.YesNoEnum;
import com.uaepay.pub.csc.configuration.CscConfiguration;
import com.uaepay.pub.csc.core.common.util.ErrorUtil;
import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.data.Constants;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.enums.AttachType;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import com.uaepay.pub.csc.domain.monitor.QueryRows;
import com.uaepay.pub.csc.domain.notify.MailAttachment;
import com.uaepay.pub.csc.domain.notify.MailExtraInfo;
import com.uaepay.pub.csc.domain.properties.MonitorProperties;
import com.uaepay.pub.csc.domainservice.compare.task.impl.CompareTaskRunnerFactoryImpl;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIterator;
import com.uaepay.pub.csc.domainservice.monitor.data.QueryDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorDefineRepository;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskCallback;
import com.uaepay.pub.csc.domainservice.monitor.task.MonitorTaskRunnerFactory;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorAction;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorActionFactory;
import com.uaepay.pub.csc.domainservice.notify.NotifyService;
import com.uaepay.pub.csc.ext.integration.UfsShareClient;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;
import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;

/**
 * @author zc
 */
@Service
public class MonitorTaskRunnerFactoryImpl implements MonitorTaskRunnerFactory {

    public MonitorTaskRunnerFactoryImpl(MonitorDefineRepository monitorDefineRepository,
        QueryDataIteratorFactory queryDataIteratorFactory, MonitorProperties monitorProperties,
        MonitorTaskRepository monitorTaskRepository, MonitorActionFactory monitorActionFactory,
        NotifyService notifyService, TransactionTemplate transactionTemplate, UfsShareClient ufsShareClient,
        CscConfiguration cscConfiguration) {
        this.monitorDefineRepository = monitorDefineRepository;
        this.queryDataIteratorFactory = queryDataIteratorFactory;
        this.monitorProperties = monitorProperties;
        this.monitorTaskRepository = monitorTaskRepository;
        this.monitorActionFactory = monitorActionFactory;
        this.notifyService = notifyService;
        this.transactionTemplate = transactionTemplate;
        this.ufsShareClient = ufsShareClient;
        this.cscConfiguration = cscConfiguration;
    }

    CscConfiguration cscConfiguration;

    UfsShareClient ufsShareClient;

    MonitorDefineRepository monitorDefineRepository;

    QueryDataIteratorFactory queryDataIteratorFactory;

    MonitorProperties monitorProperties;

    MonitorTaskRepository monitorTaskRepository;

    MonitorActionFactory monitorActionFactory;

    NotifyService notifyService;

    TransactionTemplate transactionTemplate;

    @Value("${env}")
    String environment;

    @Value("${app.default.country:}")
    String country;

    @Value("${basis.monitorDefineUrl}")
    String monitorDefineUrl;

    @Value("${basis.monitorTaskUrl}")
    String monitorTaskUrl;

    static final FormatUtil FORMAT_UTIL = new FormatUtil();

    @Override
    public Runnable create(MonitorDefine define, MonitorTask task, MonitorTaskCallback callback) {
        return new MonitorTaskRunnableImpl(define, task, callback);
    }

    /**
     * 监控任务执行器
     */
    public class MonitorTaskRunnableImpl implements Runnable {

        private static final String PARAM_DEFINE = "define";
        private static final String PARAM_TASK = "task";
        private static final String PARAM_COLUMNS = "columns";
        private static final String PARAM_DETAILS = "details";
        private static final String PARAM_ENV = "env";
        private static final String PARAM_COUNTRY = "country";
        private static final String PARAM_FORMAT_UTIL = "format";
        private static final String PARAM_MONITOR_DEFINE_URL = "monitorDefineUrl";
        private static final String PARAM_MONITOR_TASK_URL = "monitorTaskUrl";

        private Logger logger = LoggerFactory.getLogger(CompareTaskRunnerFactoryImpl.CompareTaskRunnableImpl.class);

        public MonitorTaskRunnableImpl(MonitorDefine define, MonitorTask task, MonitorTaskCallback callback) {
            this.define = define;
            this.task = task;
            this.callback = callback;
            this.monitorAction = monitorActionFactory.create(define.getMonitorType());
        }

        private MonitorDefine define;
        private MonitorTask task;
        private MonitorTaskCallback callback;
        private MonitorAction monitorAction;
        private boolean taskSaved;

        @Override
        public void run() {
            try {
                saveTask();
                MonitorResult result = executeQuery();
                updateTask(result);
                resultCallback(result);
                try {
                    doNotify(result);
                } catch (Throwable e) {
                    logger.error("通知异常", e);
                }
            } catch (Throwable e) {
                logger.error("执行异常", e);
                processError(e);
            }
        }

        private void saveTask() {
            transactionTemplate.executeWithoutResult((status) -> {
                monitorTaskRepository.saveTask(task);
                if (callback != null) {
                    callback.onSaveTask(task);
                }
            });
            taskSaved = true;
        }

        /** 执行查询 */
        private MonitorResult executeQuery() {
            logger.info("监控执行: define={}, task={}, {}~{}", task.getDefineId(), task.getTaskId(),
                DateFormatUtils.ISO_DATETIME_FORMAT.format(task.getDataBeginTime()),
                DateFormatUtils.ISO_DATETIME_FORMAT.format(task.getDataEndTime()));
            StopWatch watch = StopWatch.createStarted();
            QueryParam queryParam = new QueryParam(task.getDataBeginTime(), task.getDataEndTime());
            QueryDataIterator dataIterator = queryDataIteratorFactory.create(define, queryParam);
            int itCount = 0;
            MonitorResult result = new MonitorResult();
            while (dataIterator.hasNext()) {
                QueryRows queryRows = dataIterator.next();
                logger.info("子查询: seq={}, rows={}", ++itCount, queryRows.size());
                if (queryRows.isEmpty()) {
                    continue;
                }
                if (!result.merge(queryRows, monitorProperties.getMaxDetailCount(define.getMonitorType()),
                    define.getSplitStrategy())) {
                    logger.info("已达到最大明细数，终止执行");
                    break;
                }
            }
            monitorAction.afterQuery(define, task, result);
            watch.stop();
            task.setExecuteConsume((int)watch.getTime(TimeUnit.SECONDS));
            logger.info("结果统计: count={}, isAll={}, consume={}s", result.size(), result.isAll(),
                task.getExecuteConsume());
            return result;
        }

        /** 更新任务状态 */
        private void updateTask(MonitorResult monitorResult) {
            task.setDetailCount(monitorResult.size());
            task.setIsAllDetail(monitorResult.isAll() ? YesNoEnum.YES : YesNoEnum.NO);
            monitorAction.updateTask(define, task, monitorResult);
        }

        /** 对账结果回调 */
        private void resultCallback(MonitorResult result) {
            if (callback != null) {
                try {
                    callback.onResult(task, result);
                } catch (Throwable e) {
                    logger.error("执行结果，回调异常", e);
                }
            }
        }

        /** 通知 */
        private void doNotify(MonitorResult result) {
            Supplier<MailExtraInfo> mailSupplier = makeMailExtraSupplier(result);
            if (define.getMonitorType() == MonitorTypeEnum.REPORT) {
                if (result.size() == 0) {
                    return;
                }
                notifyService.notifyByTemplate(NotifyTemplateTypeEnum.REPORT, define.getDefineId(), null,
                    buildNotifyParam(result), define.isNotifyTeams(), mailSupplier);
            } else if (define.getMonitorType() == MonitorTypeEnum.ALARM) {
                if (!(task.getAlarmLevel() == AlarmLevelEnum.NORMAL || task.getAlarmLevel() == AlarmLevelEnum.URGENT)) {
                    return;
                }
                notifyService.notifyByTemplate(NotifyTemplateTypeEnum.ALARM, define.getDefineId(), task.getAlarmLevel(),
                    buildNotifyParam(result), true, null);
            }
        }

        /** Mail extra supplier */
        public Supplier<MailExtraInfo> makeMailExtraSupplier(MonitorResult result) {
            if (CollectionUtils.isEmpty(result.getDetails())) {
                return null;
            }
            return () -> {
                MailExtraInfo extraInfo = new MailExtraInfo();
                AttachType attachType = define.getAttachType();
                if (attachType != null) {
                    File attach = attachType.getMonitorWriterFun().apply(result);
                    String fileTag =
                        ufsShareClient.upload(attach, cscConfiguration.getAttachFileExpireDuration(), true);
                    String url = ufsShareClient.getUrl(fileTag);
                    extraInfo.addAttachment(new MailAttachment(define.getDefineName() + Constants.CSV_SUFFIX, url));
                }
                return extraInfo;
            };
        }

        private Map<String, Object> buildNotifyParam(MonitorResult result) {
            Map<String, Object> params = new HashMap<>(6);
            params.put(PARAM_DEFINE, define);
            params.put(PARAM_TASK, task);
            params.put(PARAM_ENV, environment);
            params.put(PARAM_COUNTRY, country);
            params.put(PARAM_MONITOR_DEFINE_URL, String.format(monitorDefineUrl, task.getDefineId()));
            params.put(PARAM_MONITOR_TASK_URL, String.format(monitorTaskUrl, task.getTaskId()));
            params.put(PARAM_FORMAT_UTIL, FORMAT_UTIL);
            if (result != null && CollectionUtils.isNotEmpty(result.getDetails())) {
                params.put(PARAM_COLUMNS, result.getDetails().get(0).getColumnData().getColumnInfos());
                params.put(PARAM_DETAILS, result.getDetails());
            }
            return params;
        }

        /**
         * 处理异常，异常回调
         *
         */
        private void processError(Throwable e) {
            try {
                if (taskSaved) {
                    String code = ErrorUtil.convertErrorCodeToRecord(e);
                    String message = e.getMessage();
                    task.setError(code, message);
                    monitorTaskRepository.updateError(task);
                }
            } catch (Throwable ee) {
                logger.error("执行异常，更新异常", ee);
            }
            if (callback != null) {
                try {
                    callback.onError(e);
                } catch (Throwable ce) {
                    logger.error("执行异常，回调异常", ce);
                }
            }
        }

    }

}
