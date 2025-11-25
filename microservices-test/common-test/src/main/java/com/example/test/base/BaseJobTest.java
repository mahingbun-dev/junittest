package com.example.test.base;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

/**
 * 定时任务测试基类
 * 
 * 专门用于 Job 微服务的定时任务测试
 * 支持异步等待和定时任务模拟
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public abstract class BaseJobTest {

    @BeforeEach
    protected void setUp() {
        // 配置默认的异步等待超时
        Awaitility.setDefaultTimeout(10, TimeUnit.SECONDS);
        Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
    }

    /**
     * 等待直到条件满足
     */
    protected void waitUntil(java.util.concurrent.Callable<Boolean> conditionEvaluator) {
        Awaitility.await().until(conditionEvaluator);
    }

    /**
     * 等待指定时间直到条件满足
     */
    protected void waitUntil(java.util.concurrent.Callable<Boolean> conditionEvaluator, 
                             long timeout, TimeUnit unit) {
        Awaitility.await()
                .atMost(timeout, unit)
                .until(conditionEvaluator);
    }

    /**
     * 等待一段时间（用于等待定时任务执行）
     */
    protected void waitFor(long duration, TimeUnit unit) {
        try {
            unit.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

