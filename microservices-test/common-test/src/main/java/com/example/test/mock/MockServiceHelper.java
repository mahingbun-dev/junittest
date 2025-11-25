package com.example.test.mock;

import com.example.test.util.JsonTestUtil;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Mock 服务助手
 * 
 * 使用 WireMock 模拟微服务间的 HTTP 调用
 */
public class MockServiceHelper {

    // ==================== 通用 Mock 方法 ====================

    /**
     * Mock GET 请求返回成功响应
     */
    public static void mockGetSuccess(String url, Object responseBody) {
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock GET 请求返回 404
     */
    public static void mockGetNotFound(String url) {
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":404,\"message\":\"Not Found\"}")));
    }

    /**
     * Mock POST 请求返回成功响应
     */
    public static void mockPostSuccess(String url, Object responseBody) {
        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock POST 请求返回创建成功
     */
    public static void mockPostCreated(String url, Object responseBody) {
        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock PUT 请求返回成功响应
     */
    public static void mockPutSuccess(String url, Object responseBody) {
        stubFor(put(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock DELETE 请求返回成功
     */
    public static void mockDeleteSuccess(String url) {
        stubFor(delete(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(204)));
    }

    // ==================== 错误响应 Mock ====================

    /**
     * Mock 返回 400 错误
     */
    public static void mockBadRequest(String url, String errorMessage) {
        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"" + errorMessage + "\"}")));
    }

    /**
     * Mock 返回 401 未授权
     */
    public static void mockUnauthorized(String url) {
        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":401,\"message\":\"Unauthorized\"}")));
    }

    /**
     * Mock 返回 500 服务器错误
     */
    public static void mockServerError(String url) {
        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":500,\"message\":\"Internal Server Error\"}")));
    }

    /**
     * Mock 请求超时
     */
    public static void mockTimeout(String url, int delayMillis) {
        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(delayMillis)));
    }

    // ==================== 验证方法 ====================

    /**
     * 验证请求被调用
     */
    public static void verifyGetCalled(String url) {
        WireMock.verify(getRequestedFor(urlEqualTo(url)));
    }

    /**
     * 验证请求被调用指定次数
     */
    public static void verifyGetCalled(String url, int times) {
        WireMock.verify(times, getRequestedFor(urlEqualTo(url)));
    }

    /**
     * 验证 POST 请求被调用
     */
    public static void verifyPostCalled(String url) {
        WireMock.verify(postRequestedFor(urlEqualTo(url)));
    }

    /**
     * 重置所有 Mock
     */
    public static void resetAll() {
        WireMock.reset();
    }
}

