package com.uaepay.pub.csc.ext.daemon;

import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.uaepay.job.starter.autoconfigure.annotation.ElasticJobConfig;
import com.uaepay.job.starter.base.AbstractSimpleJob;
import com.uaepay.pub.csc.domainservice.logmonitor.schedule.GatewayMonitorService;

/**
 * @author zc
 */
@ElasticJobConfig(jobCode = "csc_GatewayMonitorScheduleJob${job.suffix:}", cron = "${schedule.job.crontab}",
    shardingCount = "1", description = "网关日志监控定时任务",
    disabledEx = "${schedule.job.disabled:false}", overwriteEx = "${schedule.job.overwrite:false}")
public class GatewayMonitorJob extends AbstractSimpleJob {

    @Autowired
    GatewayMonitorService gatewayMonitorService;

    @Override
    protected String jobName() {
        return "网关日志监控任务";
    }

    @Override
    protected void processJob(ShardingContext shardingContext, String s) {
        gatewayMonitorService.runJob();
    }

}
