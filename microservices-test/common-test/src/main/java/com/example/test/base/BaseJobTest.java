package com.example.test.base;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 【定时任务/异步任务测试基类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是定时任务和异步任务？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【定时任务】
 * - 按照设定的时间规则自动执行的任务
 * - 例如：每天凌晨2点同步数据、每小时统计一次报表
 * 
 * 【异步任务】
 * - 不阻塞主线程，在后台执行的任务
 * - 例如：发送邮件、处理大文件
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 为什么异步任务测试比较困难？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 普通测试：
 * <pre>
 *   result = service.doSomething();    // 执行
 *   assertThat(result).isSuccess();    // 立即检查结果
 * </pre>
 * 
 * 异步任务测试的问题：
 * <pre>
 *   service.startAsyncTask();          // 启动异步任务
 *   assertThat(task.isCompleted());    // ❌ 错误！任务还没完成！
 *                                      // 因为任务在后台运行，需要时间
 * </pre>
 * 
 * 解决方案：使用 Awaitility 等待
 * <pre>
 *   service.startAsyncTask();          // 启动异步任务
 *   await()                            // 等待...
 *       .atMost(10, SECONDS)           // 最多等10秒
 *       .until(() -> task.isCompleted());  // 直到任务完成
 *   // 现在可以安全地检查结果了
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 Awaitility 是什么？
 * ═══════════════════════════════════════════════════════════════
 * 
 * Awaitility 是一个专门用于异步测试的库。它提供了优雅的 API 来：
 * - 等待某个条件变为 true
 * - 设置最长等待时间（超时就失败）
 * - 设置检查频率（多久检查一次条件）
 * 
 * 常用方法：
 * - await().atMost(10, SECONDS)        // 最多等待10秒
 * - await().atLeast(1, SECONDS)        // 至少等待1秒
 * - await().pollInterval(100, MILLIS)  // 每100毫秒检查一次
 * - await().until(() -> condition)     // 直到条件为 true
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * class DataSyncJobTest extends BaseJobTest {
 *     
 *     @Autowired
 *     private DataSyncJob dataSyncJob;
 *     
 *     @Autowired
 *     private JobExecutionRepository jobExecutionRepository;
 *     
 *     @Test
 *     void testAsyncDataSync() {
 *         // 1. 触发异步任务
 *         String jobId = dataSyncJob.trigger();
 *         
 *         // 2. 等待任务完成（使用继承的 waitUntil 方法）
 *         waitUntil(() -> {
 *             JobExecution execution = jobExecutionRepository.findById(jobId);
 *             return execution != null && execution.isCompleted();
 *         });
 *         
 *         // 3. 验证结果
 *         JobExecution execution = jobExecutionRepository.findById(jobId);
 *         assertThat(execution.getStatus()).isEqualTo("SUCCESS");
 *     }
 *     
 *     @Test
 *     void testWithCustomTimeout() {
 *         // 使用自定义超时时间
 *         waitUntil(
 *             () -> isJobCompleted(),    // 条件
 *             30,                        // 超时时间
 *             TimeUnit.SECONDS           // 时间单位
 *         );
 *     }
 * }
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 Lambda 表达式简介
 * ═══════════════════════════════════════════════════════════════
 * 
 * () -> expression 是 Java 8 引入的 Lambda 表达式，是一种简洁的写法。
 * 
 * 例如 () -> task.isCompleted() 等价于：
 * <pre>
 * new Callable<Boolean>() {
 *     @Override
 *     public Boolean call() {
 *         return task.isCompleted();
 *     }
 * }
 * </pre>
 * 
 * Lambda 让代码更简洁、更易读。
 */
@SpringBootTest  // 启动完整 Spring 容器
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)  // 支持 Mockito
public abstract class BaseJobTest {

    /**
     * 初始化方法 - 配置 Awaitility 的默认参数
     */
    @BeforeEach
    protected void setUp() {
        // 设置默认超时时间：10秒
        // 如果等待超过10秒条件还没满足，测试就会失败
        Awaitility.setDefaultTimeout(10, TimeUnit.SECONDS);
        
        // 设置默认轮询间隔：100毫秒
        // 每100毫秒检查一次条件是否满足
        Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
    }

    /**
     * 【等待方法】等待直到条件满足
     * 
     * 这个方法会反复检查条件，直到：
     * - 条件返回 true（成功）
     * - 超时（失败，抛出异常）
     * 
     * @param conditionEvaluator 条件表达式，返回 Boolean
     *                          通常使用 Lambda 表达式：() -> someCondition
     * 
     * 【参数类型说明】
     * Callable<Boolean> 是一个接口，表示"一个可以被调用的东西，返回 Boolean"
     * 使用 Lambda 表达式 () -> ... 来创建这个接口的实例
     * 
     * 使用示例：
     * <pre>
     * // 等待任务完成
     * waitUntil(() -> task.isCompleted());
     * 
     * // 等待列表不为空
     * waitUntil(() -> !resultList.isEmpty());
     * 
     * // 等待计数器达到某个值
     * waitUntil(() -> counter.get() >= 10);
     * </pre>
     */
    protected void waitUntil(Callable<Boolean> conditionEvaluator) {
        Awaitility.await()      // 创建等待器
                .until(conditionEvaluator);  // 等待直到条件为 true
    }

    /**
     * 【等待方法】等待直到条件满足（可指定超时时间）
     * 
     * @param conditionEvaluator 条件表达式
     * @param timeout 超时时间数值
     * @param unit 时间单位（如 TimeUnit.SECONDS）
     * 
     * 使用示例：
     * <pre>
     * // 最多等待30秒
     * waitUntil(() -> task.isCompleted(), 30, TimeUnit.SECONDS);
     * 
     * // 最多等待5分钟
     * waitUntil(() -> job.isDone(), 5, TimeUnit.MINUTES);
     * </pre>
     */
    protected void waitUntil(Callable<Boolean> conditionEvaluator,
                             long timeout, TimeUnit unit) {
        Awaitility.await()
                .atMost(timeout, unit)  // 设置最大等待时间
                .until(conditionEvaluator);
    }

    /**
     * 【等待方法】简单地等待一段时间
     * 
     * 有时候我们只需要等待固定时间，不需要检查条件。
     * 例如：等待定时任务触发。
     * 
     * @param duration 等待时长数值
     * @param unit 时间单位
     * 
     * 使用示例：
     * <pre>
     * // 等待2秒
     * waitFor(2, TimeUnit.SECONDS);
     * 
     * // 等待500毫秒
     * waitFor(500, TimeUnit.MILLISECONDS);
     * </pre>
     * 
     * 【try-catch 说明】
     * Thread.sleep() 可能会抛出 InterruptedException 异常
     * （当线程被中断时）。我们捕获这个异常并恢复中断状态。
     */
    protected void waitFor(long duration, TimeUnit unit) {
        try {
            // Thread.sleep() 让当前线程暂停指定时间
            // unit.sleep(duration) 是更优雅的写法
            unit.sleep(duration);
        } catch (InterruptedException e) {
            // 如果线程被中断，恢复中断状态
            Thread.currentThread().interrupt();
        }
    }
}
