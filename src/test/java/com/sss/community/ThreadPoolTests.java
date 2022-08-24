package com.sss.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import com.sss.community.service.AlphaService;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTests.class);

    // 1.JDK普通线程
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    // 2.JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    // 3.Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // 4.Spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private AlphaService alphaService;


    /**
     * 封装的休眠函数
     *
     * @param m
     */
    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** 1.JDK普通线程池执行任务 */
    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("Hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; ++i) {
            executorService.execute(task);
        }
        sleep(10000);
    }

    /** 2.JDK定时任务线程池 */
    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("Hello ScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    /** 3.Spring普通线程池 */
    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("Hello ThreadPoolTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }

    /** 4.Spring定时线程池 */
    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LOGGER.debug("Hello ThreadPoolTaskScheduler");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        taskScheduler.scheduleAtFixedRate(task, startTime, 1000);
        sleep(30000);
    }

    /** 5.Spring普通线程池，简单版本 */
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for (int i = 0; i < 10; i++) {
            alphaService.execute1();
        }
        sleep(10000);
    }

    /** 6.Spring定时任务线程池(简化) */
    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        sleep(11000);
    }

}
