package com.example.test.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * 【微服务集成测试基类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是微服务间调用测试？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 在微服务架构中，一个服务经常需要调用其他服务。例如：
 * - API 服务调用 Manage 服务获取用户信息
 * - Job 服务调用 Query 服务获取需要处理的数据
 * 
 * 测试时的问题：
 * - 其他服务可能没有运行
 * - 其他服务的数据状态不确定
 * - 网络不稳定会导致测试失败
 * 
 * 解决方案：使用 WireMock "假装"是其他服务
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 WireMock 是什么？
 * ═══════════════════════════════════════════════════════════════
 * 
 * WireMock 是一个 HTTP 服务模拟器。它可以：
 * - 在本地启动一个假的 HTTP 服务器
 * - 根据你设定的规则返回假数据
 * - 验证是否收到了预期的请求
 * 
 * 工作原理图解：
 * <pre>
 * 【没有 WireMock】
 * 你的服务 ──HTTP请求──→ 真实的其他服务 ──响应──→ 你的服务
 *                          ↑
 *                     可能没启动！
 *                     可能数据不对！
 * 
 * 【使用 WireMock】
 * 你的服务 ──HTTP请求──→ WireMock(假服务) ──假响应──→ 你的服务
 *                          ↑
 *                     你完全控制！
 *                     想返回什么就返回什么！
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 关键概念解释
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【static 关键字】
 * - 表示这个变量/方法属于类本身，而不是某个实例
 * - 所有测试方法共享同一个 wireMockServer
 * - 不需要每个测试都重新创建服务器
 * 
 * 【@BeforeAll / @AfterAll】
 * - 整个测试类只执行一次（所有测试方法之前/之后）
 * - 用于启动/关闭 WireMock 服务器
 * - 必须是 static 方法
 * 
 * 【@BeforeEach / @AfterEach】
 * - 每个测试方法前/后执行
 * - 用于重置 WireMock 的设置
 * - 保证每个测试都是"干净"的状态
 * 
 * 【@DynamicPropertySource】
 * - 动态设置 Spring 配置属性
 * - 把 WireMock 的地址告诉 Spring
 * - 这样你的服务就会把请求发到 WireMock
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * class ApiGatewayTest extends BaseMicroserviceTest {
 *     
 *     @Autowired
 *     private MockMvc mockMvc;
 *     
 *     @Test
 *     void testCallManageService() throws Exception {
 *         // 1. 设置 WireMock：当收到 /manage/api/users/1 请求时，返回假数据
 *         stubFor(get(urlEqualTo("/manage/api/users/1"))
 *                 .willReturn(aResponse()
 *                         .withStatus(200)
 *                         .withHeader("Content-Type", "application/json")
 *                         .withBody("{\"id\": 1, \"username\": \"testuser\"}")));
 *         
 *         // 2. 调用 API 网关接口（它会去调用"Manage服务"，实际调用的是 WireMock）
 *         mockMvc.perform(get("/api/v1/users/1"))
 *                 .andExpect(status().isOk())
 *                 .andExpect(jsonPath("$.data.username").value("testuser"));
 *         
 *         // 3. 验证 WireMock 确实收到了请求
 *         verify(getRequestedFor(urlEqualTo("/manage/api/users/1")));
 *     }
 * }
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseMicroserviceTest {

    /**
     * WireMock 服务器实例
     * 
     * static 表示这个变量属于类，所有测试方法共享
     * protected 让子类也能访问
     */
    protected static WireMockServer wireMockServer;

    /**
     * 【在所有测试开始前执行一次】启动 WireMock 服务器
     * 
     * @BeforeAll 表示在所有 @Test 方法之前执行，且只执行一次
     * static 是必须的，因为 @BeforeAll 要求方法是静态的
     */
    @BeforeAll
    static void startWireMock() {
        // 创建 WireMock 服务器，使用动态端口（避免端口冲突）
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        
        // 启动服务器
        wireMockServer.start();
        
        // 配置 WireMock 客户端，告诉它服务器的地址
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    /**
     * 【在所有测试结束后执行一次】关闭 WireMock 服务器
     */
    @AfterAll
    static void stopWireMock() {
        // 检查服务器是否存在且正在运行
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();  // 关闭服务器，释放资源
        }
    }

    /**
     * 【每个测试方法执行前】重置 WireMock 设置
     */
    @BeforeEach
    protected void setUp() {
        // resetAll() 清除所有之前设置的 Mock 规则
        // 保证每个测试都是从"干净"的状态开始
        wireMockServer.resetAll();
    }

    /**
     * 【每个测试方法执行后】清理
     */
    @AfterEach
    protected void tearDown() {
        wireMockServer.resetAll();
    }

    /**
     * 【动态设置 Spring 配置】
     * 
     * 这个方法会在 Spring 启动时被调用，用于设置配置属性。
     * 
     * @DynamicPropertySource 是 Spring 提供的注解，
     * 允许我们在测试时动态设置配置属性。
     * 
     * 工作原理：
     * 1. Spring 启动时调用这个方法
     * 2. 我们把 WireMock 的地址写入配置
     * 3. 应用中需要调用外部服务时，会使用这个地址
     * 4. 结果就是请求发送到了 WireMock 而不是真实服务
     * 
     * @param registry 配置注册器，用于添加配置属性
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // 把 WireMock 的地址设置为 "external.service.url" 配置项
        // Lambda 表达式 () -> "..." 表示一个返回字符串的函数
        registry.add("external.service.url", 
                () -> "http://localhost:" + wireMockServer.port());
    }

    /**
     * 【辅助方法】获取 WireMock 服务器的端口号
     * 
     * @return 端口号
     */
    protected int getWireMockPort() {
        return wireMockServer.port();
    }

    /**
     * 【辅助方法】获取 WireMock 服务器的完整基础 URL
     * 
     * @return 例如 "http://localhost:8080"
     */
    protected String getWireMockBaseUrl() {
        return "http://localhost:" + wireMockServer.port();
    }
}
