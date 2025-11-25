package com.example.test.mock;

import com.example.test.util.JsonTestUtil;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Manage 微服务 Mock
 * 
 * 模拟 Manage 服务的接口响应
 * 供其他微服务测试时使用
 */
public class ManageServiceMock {

    private static final String BASE_PATH = "/manage/api";

    // ==================== 用户管理接口 Mock ====================

    /**
     * Mock 获取用户信息
     */
    public static void mockGetUser(Long userId, String username, String email) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", userId);
        data.put("username", username);
        data.put("email", email);
        data.put("status", "ACTIVE");
        response.put("data", data);

        stubFor(get(urlEqualTo(BASE_PATH + "/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 用户不存在
     */
    public static void mockUserNotFound(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", "用户不存在: " + userId);

        stubFor(get(urlEqualTo(BASE_PATH + "/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 创建用户
     */
    public static void mockCreateUser(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户创建成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", userId);
        response.put("data", data);

        stubFor(post(urlEqualTo(BASE_PATH + "/users"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    // ==================== 权限管理接口 Mock ====================

    /**
     * Mock 验证用户权限
     */
    public static void mockCheckPermission(Long userId, String permission, boolean hasPermission) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", hasPermission);

        stubFor(get(urlPathEqualTo(BASE_PATH + "/permissions/check"))
                .withQueryParam("userId", equalTo(String.valueOf(userId)))
                .withQueryParam("permission", equalTo(permission))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    // ==================== 配置管理接口 Mock ====================

    /**
     * Mock 获取系统配置
     */
    public static void mockGetConfig(String key, String value) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", value);

        stubFor(get(urlEqualTo(BASE_PATH + "/configs/" + key))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }
}

