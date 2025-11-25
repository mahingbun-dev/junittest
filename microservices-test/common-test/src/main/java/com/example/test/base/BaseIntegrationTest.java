package com.example.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 【集成测试基类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是集成测试？
 * ═══════════════════════════════════════════════════════════════
 * 集成测试是测试多个组件一起工作时的行为。
 * 与单元测试的区别：
 * 
 * | 特性       | 单元测试          | 集成测试              |
 * |-----------|------------------|----------------------|
 * | Spring容器 | 不启动           | 启动完整容器           |
 * | 数据库     | 不使用/模拟       | 使用真实数据库(H2)     |
 * | 外部服务   | 模拟             | 可以真实调用或模拟      |
 * | 执行速度   | 很快(毫秒级)      | 较慢(秒级)            |
 * | 测试范围   | 单个类/方法       | 多个组件协作           |
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 这个类用到的注解详解
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【@SpringBootTest】
 * - 启动完整的 Spring Boot 应用程序
 * - 会加载所有的 Bean（Controller、Service、Repository 等）
 * - webEnvironment = RANDOM_PORT 表示使用随机端口启动 Web 服务器
 * 
 * 【@ActiveProfiles("test")】
 * - 激活名为 "test" 的配置文件
 * - 会读取 application-test.yml 中的配置
 * - 通常用于使用测试数据库、关闭不需要的功能等
 * 
 * 【@Transactional】
 * - 将测试方法包装在数据库事务中
 * - 测试结束后自动回滚，不会污染数据库
 * - 保证每个测试方法都是从"干净"的数据库状态开始
 * 
 * 【@DirtiesContext】
 * - 测试完成后重置 Spring 上下文
 * - ClassMode.AFTER_CLASS = 整个测试类执行完后才重置
 * - 避免测试之间相互影响
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * class UserIntegrationTest extends BaseIntegrationTest {
 *     
 *     @Autowired  // Spring 自动注入真实的 Service
 *     private UserService userService;
 *     
 *     @Autowired
 *     private UserRepository userRepository;
 *     
 *     @Test
 *     void testCreateAndFindUser() {
 *         // 1. 创建用户（会真实保存到数据库）
 *         UserDTO user = userService.createUser(dto);
 *         
 *         // 2. 查询用户（从真实数据库查询）
 *         UserDTO found = userService.getUserById(user.getId());
 *         
 *         // 3. 验证结果
 *         assertThat(found.getUsername()).isEqualTo(dto.getUsername());
 *         
 *         // 测试结束后，数据会自动回滚，不会留在数据库中
 *     }
 * }
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")       // 使用 test 配置文件
@Transactional               // 测试完自动回滚数据库
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)  // 测试后重置上下文
public abstract class BaseIntegrationTest {

    /**
     * 每个测试方法执行前的初始化
     * 
     * 子类可以覆盖此方法来：
     * - 清理测试数据
     * - 准备测试环境
     * - 初始化测试对象
     */
    @BeforeEach
    protected void setUp() {
        // 子类可覆盖进行初始化
    }
}
