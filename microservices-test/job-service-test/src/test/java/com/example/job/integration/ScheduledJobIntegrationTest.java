package com.example.job.integration;

import com.example.test.base.BaseJobTest;
import com.example.test.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Job 服务 - 定时任务集成测试示例
 * 
 * 测试颗粒度：集成测试
 * 测试目标：定时任务完整执行流程
 * 
 * 特点：支持异步等待，测试定时任务的真实执行
 */
@AutoConfigureMockMvc
@DisplayName("【Job服务】定时任务集成测试")
class ScheduledJobIntegrationTest extends BaseJobTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO: 注入你的 Repository 用于验证数据
    // @Autowired
    // private JobExecutionRepository jobExecutionRepository;

    private String testJobName;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        testJobName = "integration_job_" + System.currentTimeMillis();
    }

    // ==================== 任务生命周期测试 ====================

    @Nested
    @DisplayName("任务生命周期测试")
    class JobLifecycleTests {

        // @Test
        @DisplayName("完整的任务生命周期：创建 -> 执行 -> 完成")
        void fullJobLifecycle() throws Exception {
            // 1. 创建任务
            Map<String, Object> createRequest = new HashMap<>();
            createRequest.put("jobName", testJobName);
            createRequest.put("cronExpression", "0 0 * * * ?");
            createRequest.put("jobClass", "com.example.job.tasks.TestJob");
            createRequest.put("enabled", true);

            // mockMvc.perform(post("/job/api/jobs")
            //                 .contentType(MediaType.APPLICATION_JSON)
            //                 .content(toJson(createRequest)))
            //         .andDo(print())
            //         .andExpect(status().isCreated());

            // 2. 手动触发任务
            // MvcResult triggerResult = mockMvc.perform(post("/job/api/jobs/{name}/trigger", testJobName))
            //         .andExpect(status().isOk())
            //         .andReturn();
            // 
            // String executionId = JsonPath.parse(triggerResult.getResponse().getContentAsString())
            //         .read("$.data.jobId", String.class);

            // 3. 等待任务完成
            // await()
            //     .atMost(30, TimeUnit.SECONDS)
            //     .pollInterval(1, TimeUnit.SECONDS)
            //     .until(() -> {
            //         JobExecution execution = jobExecutionRepository.findById(executionId).orElse(null);
            //         return execution != null && execution.getStatus() == JobStatus.COMPLETED;
            //     });

            // 4. 验证任务执行结果
            // mockMvc.perform(get("/job/api/jobs/{id}/status", executionId))
            //         .andExpect(status().isOk())
            //         .andExpect(jsonPath("$.data.status").value("COMPLETED"));
        }
    }

    // ==================== 任务调度测试 ====================

    @Nested
    @DisplayName("任务调度测试")
    class JobSchedulingTests {

        // @Test
        @DisplayName("暂停和恢复任务")
        void pauseAndResumeJob() throws Exception {
            // 1. 创建并启用任务
            // ...

            // 2. 暂停任务
            // mockMvc.perform(post("/job/api/scheduled-jobs/{name}/pause", testJobName))
            //         .andExpect(status().isOk());

            // 3. 验证任务已暂停
            // mockMvc.perform(get("/job/api/scheduled-jobs/{name}", testJobName))
            //         .andExpect(jsonPath("$.data.status").value("PAUSED"));

            // 4. 恢复任务
            // mockMvc.perform(post("/job/api/scheduled-jobs/{name}/resume", testJobName))
            //         .andExpect(status().isOk());

            // 5. 验证任务已恢复
            // mockMvc.perform(get("/job/api/scheduled-jobs/{name}", testJobName))
            //         .andExpect(jsonPath("$.data.status").value("SCHEDULED"));
        }
    }

    // ==================== 异步任务测试 ====================

    @Nested
    @DisplayName("异步任务测试")
    class AsyncJobTests {

        // @Test
        @DisplayName("异步任务执行并等待完成")
        void asyncJob_ShouldCompleteEventually() throws Exception {
            // 使用 Awaitility 等待异步任务完成
            // await()
            //     .atMost(10, TimeUnit.SECONDS)
            //     .until(() -> isJobCompleted(testJobId));
        }
    }

    // ==================== 失败重试测试 ====================

    @Nested
    @DisplayName("失败重试测试")
    class RetryTests {

        // @Test
        @DisplayName("任务失败后自动重试")
        void failedJob_ShouldRetry() throws Exception {
            // 实现失败重试测试
        }

        // @Test
        @DisplayName("重试次数耗尽后标记失败")
        void exhaustedRetries_ShouldMarkFailed() throws Exception {
            // 实现重试次数耗尽测试
        }
    }

    // 辅助方法
    private String toJson(Object obj) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
    }
}

