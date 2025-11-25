package com.example.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 【单元测试基类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是单元测试？
 * ═══════════════════════════════════════════════════════════════
 * 单元测试是测试代码中最小的可测试单元（通常是一个方法或一个类）。
 * 它的特点是：
 * - 快速：不启动任何外部服务
 * - 隔离：不依赖数据库、网络等外部资源
 * - 可重复：每次运行结果一致
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 这个类的作用
 * ═══════════════════════════════════════════════════════════════
 * 这是所有单元测试的"父类"，其他测试类继承它后，会自动获得：
 * 1. Mockito 框架的支持（用于模拟依赖）
 * 2. 统一的初始化方法
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * // 继承这个基类
 * class UserServiceTest extends BaseUnitTest {
 *     
 *     @Mock  // 创建一个"假的" UserRepository
 *     private UserRepository userRepository;
 *     
 *     @InjectMocks  // 自动把 Mock 对象注入到 UserService 中
 *     private UserServiceImpl userService;
 *     
 *     @Test
 *     void testCreateUser() {
 *         // 测试代码...
 *     }
 * }
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 Java 语法说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【abstract 关键字】
 * - 表示这是一个"抽象类"，不能直接创建实例
 * - 必须被其他类继承后才能使用
 * - 例如：不能写 new BaseUnitTest()，但可以写 class MyTest extends BaseUnitTest
 * 
 * 【protected 关键字】
 * - 访问修饰符，表示只有子类和同包的类可以访问
 * - 比 public 更严格，比 private 更宽松
 */
@ExtendWith(MockitoExtension.class)  // 【注解说明】启用 Mockito 框架，让 @Mock 注解生效
public abstract class BaseUnitTest {

    /**
     * 【每个测试方法执行前都会调用此方法】
     * 
     * @BeforeEach 注解的作用：
     * - 在每个 @Test 方法执行之前，自动执行这个方法
     * - 用于准备测试数据、初始化对象等
     * 
     * protected 关键字：
     * - 子类可以覆盖（重写）这个方法，添加自己的初始化逻辑
     * 
     * 使用示例：
     * <pre>
     * class UserServiceTest extends BaseUnitTest {
     *     private User testUser;
     *     
     *     @Override  // 表示覆盖父类的方法
     *     protected void setUp() {
     *         super.setUp();  // 先调用父类的初始化
     *         testUser = new User();  // 然后做自己的初始化
     *         testUser.setName("测试用户");
     *     }
     * }
     * </pre>
     */
    @BeforeEach  // JUnit 5 注解：每个测试方法执行前运行
    protected void setUp() {
        // 默认实现为空
        // 子类可以通过 @Override 覆盖此方法来添加初始化逻辑
    }
}
