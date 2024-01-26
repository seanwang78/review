package com.uaepay.pub.csc.test.mocker;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.domainservice.executor.CscNormalTaskExecutor;

@Service
public class TaskExecutorMocker {

    @Autowired
    CscNormalTaskExecutor cscTaskExecutor;

    /**
     * 将线程池占满
     */
    public void mockFull(int millis) {
        // 占满线程
        while (true) {
            try {
                cscTaskExecutor.getExecutor().execute(new TaskFullRunner(millis));
            } catch (TaskRejectedException e) {
                break;
            }
        }
    }

    /**
     * 等待清空任务
     */
    public void waitClear() {
        ThreadPoolTaskExecutor executor = cscTaskExecutor.getExecutor();
        for (int i = 0; i != 100; i++) {
            if (executor.getActiveCount() != 0) {
                sleep(1000);
            } else {
                sleep(3000);
                break;
            }
        }
        System.out.println("等待清空结束: " + DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // /**
    // * 等待有空闲线程
    // */
    // public void waitAvailable() {
    // for (int i = 0; i != 100; i++) {
    // if (cscTaskExecutor.isFull()) {
    // sleep(1000);
    // }
    // }
    // System.out.println("等待结束: " + DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date()));
    // }

    private static class TaskFullRunner implements Runnable {

        public TaskFullRunner(int millis) {
            this.millis = millis;
        }

        private int millis;

        @Override
        public void run() {
            System.out.println("占坑");
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("占坑结束");
        }
    }

}
