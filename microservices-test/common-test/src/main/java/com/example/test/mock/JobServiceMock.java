package com.example.test.mock;

import com.example.test.util.JsonTestUtil;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Job 微服务 Mock
 * 
 * 模拟 Job 任务调度服务的接口响应
 * 供其他微服务测试时使用
 */
public class JobServiceMock {

    private static final String BASE_PATH = "/job/api";

    // ==================== 任务调度接口 Mock ====================

    /**
     * Mock 触发任务
     */
    public static void mockTriggerJob(String jobName, String jobId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务触发成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("jobId", jobId);
        data.put("jobName", jobName);
        data.put("status", "RUNNING");
        response.put("data", data);

        stubFor(post(urlEqualTo(BASE_PATH + "/jobs/" + jobName + "/trigger"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 获取任务状态
     */
    public static void mockGetJobStatus(String jobId, String status) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("jobId", jobId);
        data.put("status", status);
        data.put("progress", status.equals("COMPLETED") ? 100 : 50);
        response.put("data", data);

        stubFor(get(urlEqualTo(BASE_PATH + "/jobs/" + jobId + "/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 任务完成
     */
    public static void mockJobCompleted(String jobId, Object result) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("jobId", jobId);
        data.put("status", "COMPLETED");
        data.put("progress", 100);
        data.put("result", result);
        response.put("data", data);

        stubFor(get(urlEqualTo(BASE_PATH + "/jobs/" + jobId + "/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 任务失败
     */
    public static void mockJobFailed(String jobId, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("jobId", jobId);
        data.put("status", "FAILED");
        data.put("errorMessage", errorMessage);
        response.put("data", data);

        stubFor(get(urlEqualTo(BASE_PATH + "/jobs/" + jobId + "/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 取消任务
     */
    public static void mockCancelJob(String jobId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务取消成功");

        stubFor(post(urlEqualTo(BASE_PATH + "/jobs/" + jobId + "/cancel"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    // ==================== 定时任务接口 Mock ====================

    /**
     * Mock 获取定时任务列表
     */
    public static void mockGetScheduledJobs(Object jobs) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", jobs);

        stubFor(get(urlEqualTo(BASE_PATH + "/scheduled-jobs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 暂停定时任务
     */
    public static void mockPauseScheduledJob(String jobName) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "定时任务已暂停");

        stubFor(post(urlEqualTo(BASE_PATH + "/scheduled-jobs/" + jobName + "/pause"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 恢复定时任务
     */
    public static void mockResumeScheduledJob(String jobName) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "定时任务已恢复");

        stubFor(post(urlEqualTo(BASE_PATH + "/scheduled-jobs/" + jobName + "/resume"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }
}

