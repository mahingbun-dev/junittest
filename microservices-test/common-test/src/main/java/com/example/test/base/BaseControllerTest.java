package com.example.test.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 【Controller 控制器测试基类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是 Controller 测试？
 * ═══════════════════════════════════════════════════════════════
 * Controller 是处理 HTTP 请求的类（就是定义 API 接口的地方）。
 * Controller 测试的目的是验证：
 * - HTTP 请求是否正确路由到对应方法
 * - 请求参数是否正确解析
 * - 响应状态码是否正确（200、404、500等）
 * - 响应内容是否符合预期
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 MockMvc 是什么？
 * ═══════════════════════════════════════════════════════════════
 * MockMvc 是 Spring 提供的"模拟 HTTP 请求"的工具。
 * 它可以在不启动真正服务器的情况下，模拟发送 HTTP 请求。
 * 
 * 类比理解：
 * - 真实请求：用户 → 浏览器 → 网络 → 服务器 → Controller
 * - MockMvc：  测试代码 → MockMvc → 直接调用 Controller
 * 
 * 好处：
 * - 不需要启动服务器，测试更快
 * - 可以精确控制请求内容
 * - 可以验证响应的每个细节
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * // 注意：需要配合 @WebMvcTest 注解使用
 * @WebMvcTest(UserController.class)  // 只加载 UserController
 * class UserControllerTest extends BaseControllerTest {
 *     
 *     @MockBean  // 创建假的 Service（因为我们只测试 Controller）
 *     private UserService userService;
 *     
 *     @Test
 *     void testGetUser() throws Exception {
 *         // 1. 设置 Mock 行为：当调用 getUserById(1L) 时返回测试用户
 *         given(userService.getUserById(1L)).willReturn(testUser);
 *         
 *         // 2. 发送模拟的 GET 请求
 *         mockMvc.perform(
 *                 get("/api/users/1")  // GET 请求到 /api/users/1
 *             )
 *             .andDo(print())          // 打印请求和响应详情（调试用）
 *             .andExpect(status().isOk())  // 期望返回 200 状态码
 *             .andExpect(jsonPath("$.data.id").value(1));  // 期望 JSON 中 data.id = 1
 *     }
 *     
 *     @Test
 *     void testCreateUser() throws Exception {
 *         // POST 请求示例
 *         UserDTO inputDTO = new UserDTO();
 *         inputDTO.setUsername("newuser");
 *         
 *         mockMvc.perform(
 *                 post("/api/users")                    // POST 请求
 *                 .contentType(MediaType.APPLICATION_JSON)  // 请求体类型是 JSON
 *                 .content(toJson(inputDTO))            // 请求体内容
 *             )
 *             .andExpect(status().isCreated());  // 期望返回 201 状态码
 *     }
 * }
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 注解说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【@AutoConfigureMockMvc】
 * - 自动配置 MockMvc 对象
 * - 让我们可以直接 @Autowired 注入 MockMvc
 * 
 * 【@ActiveProfiles("test")】
 * - 使用测试环境的配置文件
 * 
 * 【@Autowired】
 * - Spring 的依赖注入注解
 * - 告诉 Spring："请把这个类型的 Bean 给我"
 * - Spring 会自动找到并赋值
 */
@AutoConfigureMockMvc  // 自动配置 MockMvc
@ActiveProfiles("test")
public abstract class BaseControllerTest {

    /**
     * MockMvc - 模拟 HTTP 请求的工具
     * 
     * @Autowired 注解让 Spring 自动注入这个对象
     * protected 让子类也能使用这个对象
     */
    @Autowired
    protected MockMvc mockMvc;

    /**
     * ObjectMapper - JSON 序列化/反序列化工具
     * 
     * 用途：
     * - toJson(): 把 Java 对象转成 JSON 字符串
     * - fromJson(): 把 JSON 字符串转成 Java 对象
     * 
     * 例如：
     * - UserDTO 对象 → {"username": "test", "email": "test@example.com"}
     */
    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    protected void setUp() {
        // 子类可覆盖进行初始化
    }

    /**
     * 【工具方法】把 Java 对象转换成 JSON 字符串
     * 
     * 为什么需要这个方法？
     * - HTTP 请求的 body 是字符串格式
     * - 我们通常用 Java 对象来组织数据
     * - 发送请求前需要把对象转成 JSON 字符串
     * 
     * @param obj 任意 Java 对象
     * @return JSON 格式的字符串
     * @throws Exception 转换失败时抛出异常
     * 
     * 使用示例：
     * <pre>
     * UserDTO user = new UserDTO();
     * user.setUsername("test");
     * String json = toJson(user);
     * // json = {"username": "test"}
     * </pre>
     */
    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 【工具方法】把 JSON 字符串转换成 Java 对象
     * 
     * 为什么需要这个方法？
     * - HTTP 响应的 body 是字符串格式
     * - 我们需要把它转成 Java 对象才方便操作
     * 
     * @param json JSON 格式的字符串
     * @param clazz 目标类型的 Class 对象
     * @param <T> 泛型，表示返回值类型与 clazz 参数一致
     * @return 转换后的 Java 对象
     * 
     * 【Java 泛型语法说明】
     * - <T> 是泛型声明，表示这个方法使用了泛型
     * - Class<T> clazz 表示传入一个类型信息
     * - 返回值 T 会根据传入的 clazz 自动确定类型
     * 
     * 使用示例：
     * <pre>
     * String json = "{\"username\": \"test\"}";
     * UserDTO user = fromJson(json, UserDTO.class);
     * // 现在 user.getUsername() = "test"
     * </pre>
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}
