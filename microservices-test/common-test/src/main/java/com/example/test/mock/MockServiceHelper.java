package com.example.test.mock;

import com.example.test.util.JsonTestUtil;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * 【Mock 服务助手】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 这个类是干什么的？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 这是一个使用 WireMock 模拟 HTTP 服务的工具类。
 * 
 * 简单理解：
 * - 你的代码需要调用其他服务的 API
 * - 测试时其他服务可能没有运行
 * - 这个类帮你"假装"有一个服务在运行
 * - 你可以控制这个"假服务"返回什么数据
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 WireMock 的工作原理
 * ═══════════════════════════════════════════════════════════════
 * 
 * WireMock 使用"存根"（stub）来定义模拟行为：
 * 
 * <pre>
 * stubFor(                           // 创建一个存根规则
 *     get(urlEqualTo("/api/user"))   // 当收到 GET /api/user 请求时
 *     .willReturn(                   // 返回以下响应
 *         aResponse()
 *             .withStatus(200)       // HTTP 状态码 200
 *             .withBody("...")       // 响应内容
 *     )
 * );
 * </pre>
 * 
 * 就像是在说："如果有人问你 /api/user，你就回答 ..."
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 HTTP 状态码速查
 * ═══════════════════════════════════════════════════════════════
 * 
 * | 状态码 | 含义           | 常见场景                    |
 * |-------|---------------|----------------------------|
 * | 200   | 成功           | 正常返回数据                |
 * | 201   | 创建成功        | POST 创建资源成功           |
 * | 204   | 无内容         | DELETE 删除成功，无需返回数据 |
 * | 400   | 请求错误        | 参数错误、格式错误           |
 * | 401   | 未授权         | 未登录、Token 无效          |
 * | 404   | 未找到         | 资源不存在                  |
 * | 500   | 服务器错误      | 服务器内部错误               |
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * // 示例1：模拟 GET 请求成功
 * UserDTO user = new UserDTO();
 * user.setId(1L);
 * user.setUsername("testuser");
 * MockServiceHelper.mockGetSuccess("/api/users/1", user);
 * 
 * // 示例2：模拟资源不存在
 * MockServiceHelper.mockGetNotFound("/api/users/999");
 * 
 * // 示例3：模拟服务器错误
 * MockServiceHelper.mockServerError("/api/users/1");
 * 
 * // 示例4：模拟超时（用于测试超时处理）
 * MockServiceHelper.mockTimeout("/api/slow", 5000);  // 延迟5秒
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 静态导入说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * 文件开头的 static import 让我们可以直接使用方法名，而不需要类名前缀：
 * 
 * <pre>
 * // 没有静态导入
 * WireMock.stubFor(WireMock.get(...));
 * 
 * // 有静态导入后
 * stubFor(get(...));  // 更简洁
 * </pre>
 */
public class MockServiceHelper {

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    通用成功响应 Mock                        ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * Mock GET 请求返回成功响应
     * 
     * @param url 请求的 URL 路径，如 "/api/users/1"
     * @param responseBody 响应体内容，会自动转成 JSON
     * 
     * 使用示例：
     * <pre>
     * UserDTO user = new UserDTO();
     * user.setId(1L);
     * mockGetSuccess("/api/users/1", user);
     * // 当请求 GET /api/users/1 时，会返回 user 的 JSON
     * </pre>
     */
    public static void mockGetSuccess(String url, Object responseBody) {
        // stubFor: 创建一个存根规则
        // get(urlEqualTo(url)): 匹配 GET 请求到指定 URL
        // willReturn: 定义返回的响应
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)  // HTTP 200 成功
                        .withHeader("Content-Type", "application/json")  // 响应类型是 JSON
                        .withBody(JsonTestUtil.toJson(responseBody))));  // 响应体
    }

    /**
     * Mock GET 请求返回 404（资源未找到）
     * 
     * @param url 请求的 URL 路径
     * 
     * 使用场景：
     * - 测试查询不存在的资源时的处理逻辑
     * - 测试错误处理代码
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
     * 
     * @param url 请求的 URL 路径
     * @param responseBody 响应体内容
     */
    public static void mockPostSuccess(String url, Object responseBody) {
        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock POST 请求返回创建成功（201）
     * 
     * @param url 请求的 URL 路径
     * @param responseBody 响应体内容（通常是创建的资源）
     * 
     * HTTP 201 表示资源创建成功，是 POST 创建操作的标准响应码
     */
    public static void mockPostCreated(String url, Object responseBody) {
        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(201)  // HTTP 201 Created
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock PUT 请求返回成功响应
     * 
     * PUT 通常用于更新资源
     * 
     * @param url 请求的 URL 路径
     * @param responseBody 响应体内容（通常是更新后的资源）
     */
    public static void mockPutSuccess(String url, Object responseBody) {
        stubFor(put(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(responseBody))));
    }

    /**
     * Mock DELETE 请求返回成功（204 无内容）
     * 
     * @param url 请求的 URL 路径
     * 
     * HTTP 204 表示操作成功但没有返回内容，是 DELETE 的标准响应码
     */
    public static void mockDeleteSuccess(String url) {
        stubFor(delete(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(204)));  // 204 No Content
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    错误响应 Mock                           ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * Mock 返回 400 错误（请求错误）
     * 
     * @param url 请求的 URL 路径
     * @param errorMessage 错误信息
     * 
     * 使用场景：测试参数校验失败等场景
     * 
     * 【any() 方法说明】
     * any(urlEqualTo(url)) 表示匹配任何 HTTP 方法（GET、POST、PUT、DELETE 等）
     */
    public static void mockBadRequest(String url, String errorMessage) {
        stubFor(any(urlEqualTo(url))  // any 匹配任何 HTTP 方法
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"" + errorMessage + "\"}")));
    }

    /**
     * Mock 返回 401 未授权
     * 
     * @param url 请求的 URL 路径
     * 
     * 使用场景：
     * - 测试未登录访问
     * - 测试 Token 过期
     * - 测试权限不足
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
     * 
     * @param url 请求的 URL 路径
     * 
     * 使用场景：
     * - 测试服务不可用时的处理
     * - 测试熔断器是否正常工作
     * - 测试错误重试逻辑
     */
    public static void mockServerError(String url) {
        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":500,\"message\":\"Internal Server Error\"}")));
    }

    /**
     * Mock 请求超时（延迟响应）
     * 
     * @param url 请求的 URL 路径
     * @param delayMillis 延迟时间（毫秒）
     * 
     * 使用场景：
     * - 测试超时处理逻辑
     * - 测试超时重试
     * - 测试用户友好的超时提示
     * 
     * 【withFixedDelay 说明】
     * withFixedDelay(5000) 表示延迟 5000 毫秒（5秒）后才返回响应
     * 如果调用方设置的超时时间小于这个值，就会触发超时异常
     */
    public static void mockTimeout(String url, int delayMillis) {
        stubFor(any(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(delayMillis)));  // 延迟响应
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                      验证方法                              ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * 验证 GET 请求被调用过
     * 
     * @param url 请求的 URL 路径
     * 
     * 使用场景：
     * - 确认代码确实发送了 HTTP 请求
     * - 验证请求路径是否正确
     * 
     * 使用示例：
     * <pre>
     * // 执行测试代码...
     * userService.getUser(1L);
     * 
     * // 验证是否调用了正确的 API
     * MockServiceHelper.verifyGetCalled("/api/users/1");
     * </pre>
     */
    public static void verifyGetCalled(String url) {
        // WireMock.verify 用于验证请求
        // getRequestedFor 指定要验证的是 GET 请求
        WireMock.verify(getRequestedFor(urlEqualTo(url)));
    }

    /**
     * 验证 GET 请求被调用了指定次数
     * 
     * @param url 请求的 URL 路径
     * @param times 预期的调用次数
     * 
     * 使用场景：
     * - 验证重试逻辑是否正确（应该重试 N 次）
     * - 验证缓存是否生效（应该只调用 1 次）
     */
    public static void verifyGetCalled(String url, int times) {
        WireMock.verify(times, getRequestedFor(urlEqualTo(url)));
    }

    /**
     * 验证 POST 请求被调用过
     * 
     * @param url 请求的 URL 路径
     */
    public static void verifyPostCalled(String url) {
        WireMock.verify(postRequestedFor(urlEqualTo(url)));
    }

    /**
     * 重置所有 Mock 设置
     * 
     * 清除之前设置的所有存根规则和请求记录。
     * 通常在 @BeforeEach 或 @AfterEach 中调用，
     * 确保每个测试都从干净状态开始。
     */
    public static void resetAll() {
        WireMock.reset();
    }
}
