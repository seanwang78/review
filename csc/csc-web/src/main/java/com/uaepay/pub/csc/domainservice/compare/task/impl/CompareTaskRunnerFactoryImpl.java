package com.uaepay.pub.csc.domainservice.compare.task.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.common.util.ErrorUtil;
import com.uaepay.pub.csc.core.common.util.FormatUtil;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDetail;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareResult;
import com.uaepay.pub.csc.domain.compare.ErrorDetail;
import com.uaepay.pub.csc.domain.compare.SrcRows;
import com.uaepay.pub.csc.domain.compare.TargetRows;
import com.uaepay.pub.csc.domain.data.QueryParam;
import com.uaepay.pub.csc.domain.data.RowData;
import com.uaepay.pub.csc.domain.enums.NotifyTemplateTypeEnum;
import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.compare.check.CompareService;
import com.uaepay.pub.csc.domainservice.compare.compensate.CompensateService;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIterator;
import com.uaepay.pub.csc.domainservice.compare.data.SrcDataIteratorFactory;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessor;
import com.uaepay.pub.csc.domainservice.compare.data.TargetDataAccessorFactory;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareDefineRepository;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareTaskRepository;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskCallback;
import com.uaepay.pub.csc.domainservice.compare.task.CompareTaskRunnerFactory;
import com.uaepay.pub.csc.domainservice.notify.NotifyService;

/**
 * @author zc
 */
@Repository
public class CompareTaskRunnerFactoryImpl implements CompareTaskRunnerFactory {

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    CompareTaskRepository compareTaskRepository;

    @Autowired
    CompareDefineRepository compareDefineRepository;

    @Autowired
    SrcDataIteratorFactory srcDataIteratorFactory;

    @Autowired
    TargetDataAccessorFactory targetDataAccessorFactory;

    @Autowired
    CompareService compareService;

    @Autowired
    CompareProperties compareProperties;

    @Autowired
    CompensateService compensateService;

    @Autowired
    NotifyService notifyService;

    @Value("${env}")
    String environment;

    @Value("${app.default.country:}")
    String country;

    @Value("${basis.compareDefineUrl}")
    String compareDefineUrl;

    @Value("${basis.compareTaskUrl}")
    String compareTaskUrl;

    static final FormatUtil FORMAT_UTIL = new FormatUtil();

    @Override
    public Runnable create(CompareTask task, CompareTaskCallback callback) {
        return new CompareTaskRunnableImpl(task, callback);
    }

    public class CompareTaskRunnableImpl implements Runnable {

        private static final String PARAM_DEFINE = "define";
        private static final String PARAM_TASK = "task";
        private static final String PARAM_RESULT = "result";
        private static final String PARAM_ENV = "env";
        private static final String PARAM_COUNTRY = "country";
        private static final String PARAM_FORMAT_UTIL = "format";
        private static final String PARAM_COMPARE_DEFINE_URL = "compareDefineUrl";
        private static final String PARAM_COMPARE_TASK_URL = "compareTaskUrl";

        private Logger logger = LoggerFactory.getLogger(CompareTaskRunnableImpl.class);

        public CompareTaskRunnableImpl(CompareTask task, CompareTaskCallback callback) {
            this.task = task;
            this.callback = callback;
        }

        private CompareTask task;
        private CompareTaskCallback callback;
        private CompareDefine define;
        private boolean taskSaved;

        @Override
        public void run() {
            try {
                // if (!checkStart()) {
                // return;
                // }
                saveTask();
                initialize();
                CompareResult result = processCompare();
                fillCompensateFlag(result);
                updateTask(result);
                resultCallback(result);
                try {
                    doNotify(result);
                } catch (Throwable e) {
                    logger.error("通知异常", e);
                }
            } catch (Throwable e) {
                logger.error("对账异常", e);
                processError(e);
            }
        }

        // /** 校验启动 */
        // private boolean checkStart() {
        // if (callback != null) {
        // boolean pass = callback.checkStart(task);
        // if (!pass) {
        // logger.info("任务启动验证不通过，不继续执行");
        // }
        // return pass;
        // }
        // return true;
        // }

        private void saveTask() {
            transactionTemplate.executeWithoutResult((status) -> {
                compareTaskRepository.saveTask(task);
                if (callback != null) {
                    callback.onSaveTask(task);
                }
            });
            taskSaved = true;
        }

        /** 初始化 */
        private void initialize() {
            define = compareDefineRepository.getByDefineId(task.getDefineId());
            if (define == null) {
                throw new FailException(CommonReturnCode.CONFIG_ERROR, "define not found");
            }
        }

        /** 对账 */
        private CompareResult processCompare() {
            logger.info("对账开始: define={}, task={}, {}~{}", task.getDefineId(), task.getTaskId(),
                DateFormatUtils.ISO_DATETIME_FORMAT.format(task.getDataBeginTime()),
                DateFormatUtils.ISO_DATETIME_FORMAT.format(task.getDataEndTime()));
            StopWatch watch = StopWatch.createStarted();
            QueryParam queryParam = new QueryParam(task.getDataBeginTime(), task.getDataEndTime());
            SrcDataIterator srcDataIterator = srcDataIteratorFactory.create(define.getSrcDatasourceCode(),
                define.getSrcSqlTemplate(), define.getSrcSplitMinutes(), define.getSrcRelateField(), queryParam);
            TargetDataAccessor targetDataAccessor = targetDataAccessorFactory.create(define.getTargetDatasourceCode(),
                define.getTargetSqlTemplate(), define.getTargetRelateField(), define.getTargetShardingExpression());
            CompareResult compareResult = new CompareResult();
            int itCount = 0;
            while (srcDataIterator.hasNext()) {
                SrcRows srcRows = srcDataIterator.next();
                logger.info("子查询: seq={}, rows={}", ++itCount, srcRows.size());
                if (srcRows.isEmpty()) {
                    continue;
                }
                TargetRows targetRows = targetDataAccessor.queryTargetRows(srcRows.getRelateValues());
                CompareResult subResult = compareService.compare(srcRows, targetRows, define.getCheckExpression());
                compareResult.merge(subResult, compareProperties.getMaxErrorDetailCount());
                if (compareResult.getErrorCount() >= compareProperties.getMaxErrorDetailCount()) {
                    compareResult.setComplete(false);
                    logger.info("已达到最大错误数，终止执行");
                    break;
                }
            }
            watch.stop();
            task.setCompareConsume((int)watch.getTime(TimeUnit.SECONDS));
            logger.info("对账结果统计: pass={}, error={}, complete={}, consume={}s", compareResult.getPassCount(),
                compareResult.getErrorCount(), compareResult.isComplete(), task.getCompareConsume());
            return compareResult;
        }

        /**
         * 填写补单标志
         * 
         * @return 是否有补单记录
         */
        private void fillCompensateFlag(CompareResult compareResult) {
            if (callback != null && !callback.enableCompensate()) {
                return;
            }
            boolean compensate = false;
            for (ErrorDetail detail : compareResult.getMismatchDetails()) {
                compensate =
                    compensate | compensateService.fillCompensateFlag(detail, define.getCompensateExpression());
            }
            for (ErrorDetail detail : compareResult.getLackDetails()) {
                compensate =
                    compensate | compensateService.fillCompensateFlag(detail, define.getCompensateExpression());
            }
            if (compensate) {
                compareResult.setContainsCompensate(true);
            }
        }

        /** 更新任务状态 */
        private void updateTask(CompareResult compareResult) {
            task.setCompareStatistic(compareResult.buildCompareStatistic());
            if (compareResult.getErrorCount() == 0) {
                compareTaskRepository.updateSuccess(task);
            } else {
                List<ErrorDetail> errorDetails = new ArrayList<>();
                errorDetails.addAll(compareResult.getLackDetails());
                errorDetails.addAll(compareResult.getMismatchDetails());
                List<CompareDetail> details = new ArrayList<>();
                for (ErrorDetail errorDetail : errorDetails) {
                    CompareDetail detail = new CompareDetail();
                    detail.setTaskId(task.getTaskId());
                    detail.setRelateIdentity(errorDetail.getRelateIdentity());
                    detail.setSrcData(buildDataString(errorDetail.getSrcDataList()));
                    detail.setTargetData(buildDataString(errorDetail.getTargetDataList()));
                    detail.setCompareStatus(errorDetail.getCompareStatus());
                    detail.setErrorMessage(errorDetail.getErrorMessage());
                    detail.setCompensateFlag(errorDetail.getCompensateFlag());
                    if (StringUtils.isNotBlank(errorDetail.getNotifyStatus())) {
                        detail.putExtension(CompareDetail.NOTIFY_STATUS, errorDetail.getNotifyStatus());
                    }
                    details.add(detail);
                }
                logger.info("保存明细: count={}", details.size());
                compareTaskRepository.updateFail(task, details, compareResult.isContainsCompensate());
            }
        }

        private String buildDataString(List<RowData> rowDataList) {
            if (CollectionUtils.isEmpty(rowDataList)) {
                return null;
            } else if (rowDataList.size() == 1) {
                return rowDataList.get(0).toString();
            } else {
                return StringUtils.join(rowDataList, "; ");
            }
        }

        /** 处理异常，异常回调 */
        private void processError(Throwable e) {
            try {
                if (taskSaved) {
                    String code = ErrorUtil.convertErrorCodeToRecord(e);
                    String message = e.getMessage();
                    task.setError(code, message);
                    compareTaskRepository.updateError(task);
                }
            } catch (Throwable ee) {
                logger.error("对账异常，更新异常", ee);
            }
            if (callback != null) {
                try {
                    callback.onError(e);
                } catch (Throwable ce) {
                    logger.error("对账异常，回调异常", ce);
                }
            }
        }

        /** 对账结果回调 */
        private void resultCallback(CompareResult result) {
            if (callback != null) {
                try {
                    callback.onResult(task, result);
                } catch (Throwable e) {
                    logger.error("对账结果，回调异常", e);
                }
            }
        }

        /** 通知 */
        private void doNotify(CompareResult result) {
            if (result.getErrorCount() == 0) {
                return;
            }
            notifyService.notifyByTemplate(NotifyTemplateTypeEnum.COMPARE, define.getDefineId(), null,
                buildNotifyParam(result), true, null);
        }

        private Map<String, Object> buildNotifyParam(CompareResult result) {
            Map<String, Object> params = new HashMap<>(5);
            params.put(PARAM_DEFINE, define);
            params.put(PARAM_TASK, task);
            params.put(PARAM_ENV, environment);
            params.put(PARAM_COUNTRY, country);
            params.put(PARAM_COMPARE_DEFINE_URL, String.format(compareDefineUrl, task.getDefineId()));
            params.put(PARAM_COMPARE_TASK_URL, String.format(compareTaskUrl, task.getTaskId()));
            params.put(PARAM_FORMAT_UTIL, FORMAT_UTIL);
            params.put(PARAM_RESULT, result);
            return params;
        }

    }

}
