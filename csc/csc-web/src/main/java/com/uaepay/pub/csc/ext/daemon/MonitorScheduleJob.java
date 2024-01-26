package com.uaepay.pub.csc.ext.daemon;

import com.uaepay.pub.csc.domainservice.monitor.schedule.MonitorScheduleJobService;
import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.uaepay.job.starter.autoconfigure.annotation.ElasticJobConfig;
import com.uaepay.job.starter.base.AbstractSimpleJob;
import com.uaepay.pub.csc.domainservice.compare.schedule.CompareScheduleJobService;

/**
 * @author zc
 */
@ElasticJobConfig(jobCode = "csc_monitorScheduleJob${job.suffix:}", cron = "${schedule.job.crontab}",
    shardingCount = "${schedule.job.shardingCount}", description = "监控计划定时任务",
    disabledEx = "${schedule.job.disabled:false}", overwriteEx = "${schedule.job.overwrite:false}")
public class MonitorScheduleJob extends AbstractSimpleJob {

    @Autowired
    MonitorScheduleJobService monitorScheduleJobService;

    @Override
    protected String jobName() {
        return "对账计划定时任务";
    }

    @Override
    protected void processJob(ShardingContext shardingContext, String s) {
        monitorScheduleJobService.runJob(shardingContext.getShardingItem(), shardingContext.getShardingTotalCount());
    }

}
