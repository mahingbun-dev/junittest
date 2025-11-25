package com.example.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 【Repository 数据访问层测试基类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是 Repository？
 * ═══════════════════════════════════════════════════════════════
 * Repository 是数据访问层，负责与数据库交互。
 * 在 Spring Data JPA 中，Repository 是一个接口，
 * Spring 会自动帮我们实现增删改查等基本操作。
 * 
 * 例如：
 * <pre>
 * public interface UserRepository extends JpaRepository<User, Long> {
 *     // 自动实现：save(), findById(), findAll(), delete() 等
 *     
 *     // 自定义查询（Spring 根据方法名自动实现）
 *     Optional<User> findByUsername(String username);
 *     List<User> findByStatus(String status);
 * }
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 @DataJpaTest 注解详解
 * ═══════════════════════════════════════════════════════════════
 * 
 * @DataJpaTest 是专门用于测试 JPA Repository 的注解，它会：
 * 
 * 1. 【只加载 JPA 相关组件】
 *    - 只加载 Repository、Entity、JPA 配置
 *    - 不加载 Controller、Service 等其他组件
 *    - 启动速度比 @SpringBootTest 快很多
 * 
 * 2. 【配置内存数据库 H2】
 *    - 自动使用 H2 内存数据库（不需要真实数据库）
 *    - 每次测试都是全新的数据库
 *    - 测试结束后数据自动清除
 * 
 * 3. 【配置事务自动回滚】
 *    - 每个测试方法结束后自动回滚
 *    - 测试之间不会相互影响
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 TestEntityManager 是什么？
 * ═══════════════════════════════════════════════════════════════
 * TestEntityManager 是测试专用的数据库操作工具，常用方法：
 * 
 * - persistAndFlush(entity)：保存实体并立即写入数据库
 * - find(Class, id)：根据 ID 查找实体
 * - clear()：清除缓存，强制下次查询从数据库读取
 * 
 * 为什么要用 TestEntityManager 而不是 Repository？
 * - Repository 是我们要测试的对象
 * - TestEntityManager 用于准备测试数据和验证结果
 * - 避免"用被测对象验证被测对象"的循环
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * class UserRepositoryTest extends BaseRepositoryTest {
 *     
 *     @Autowired
 *     private UserRepository userRepository;  // 被测试的 Repository
 *     
 *     @Autowired
 *     private TestEntityManager entityManager;  // 用于准备数据和验证
 *     
 *     private User savedUser;  // 测试用的用户
 *     
 *     @BeforeEach
 *     void setUp() {
 *         // 准备测试数据
 *         User user = new User();
 *         user.setUsername("testuser");
 *         user.setEmail("test@example.com");
 *         
 *         // 使用 entityManager 保存（不是用 repository）
 *         savedUser = entityManager.persistAndFlush(user);
 *     }
 *     
 *     @Test
 *     void findByUsername_ShouldReturnUser() {
 *         // 使用 repository 查询（这是我们要测试的）
 *         Optional<User> found = userRepository.findByUsername("testuser");
 *         
 *         // 验证结果
 *         assertThat(found).isPresent();
 *         assertThat(found.get().getId()).isEqualTo(savedUser.getId());
 *     }
 * }
 * </pre>
 */
@DataJpaTest  // 专门用于 JPA 测试的注解，只加载数据库相关组件
@ActiveProfiles("test")  // 使用 test 配置文件
public abstract class BaseRepositoryTest {

    /**
     * 每个测试方法执行前的初始化
     * 
     * 子类通常会在这里：
     * - 清理测试数据
     * - 准备测试用的实体对象
     */
    @BeforeEach
    protected void setUp() {
        // 子类可覆盖进行初始化
    }
}
