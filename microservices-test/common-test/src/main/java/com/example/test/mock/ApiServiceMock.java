package com.example.test.mock;

import com.example.test.util.JsonTestUtil;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * API 微服务 Mock
 * 
 * 模拟 API 网关服务的接口响应
 * 供其他微服务测试时使用
 */
public class ApiServiceMock {

    private static final String BASE_PATH = "/api/v1";

    // ==================== 认证接口 Mock ====================

    /**
     * Mock Token 验证成功
     */
    public static void mockValidateTokenSuccess(String token, Long userId, String username) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", username);
        data.put("valid", true);
        response.put("data", data);

        stubFor(post(urlEqualTo(BASE_PATH + "/auth/validate"))
                .withHeader("Authorization", equalTo("Bearer " + token))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock Token 验证失败
     */
    public static void mockValidateTokenFailed(String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 401);
        response.put("message", "Token 无效或已过期");

        stubFor(post(urlEqualTo(BASE_PATH + "/auth/validate"))
                .withHeader("Authorization", equalTo("Bearer " + token))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 登录成功
     */
    public static void mockLoginSuccess(String username, String password, String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "登录成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("expiresIn", 7200);
        response.put("data", data);

        stubFor(post(urlEqualTo(BASE_PATH + "/auth/login"))
                .withRequestBody(matchingJsonPath("$.username", equalTo(username)))
                .withRequestBody(matchingJsonPath("$.password", equalTo(password)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    // ==================== 通用业务接口 Mock ====================

    /**
     * Mock 获取数据列表
     */
    public static void mockGetList(String resource, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);

        stubFor(get(urlPathEqualTo(BASE_PATH + "/" + resource))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 限流
     */
    public static void mockRateLimited(String url) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 429);
        response.put("message", "请求过于频繁，请稍后再试");

        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }
}

