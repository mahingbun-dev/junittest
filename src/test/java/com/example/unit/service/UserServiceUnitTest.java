package com.example.unit.service;

import com.example.base.BaseUnitTest;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.UserRepository;
import com.example.service.impl.UserServiceImpl;
import com.example.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * 【UserService 单元测试示例】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 这个测试类展示了什么？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 这是一个完整的 Service 层单元测试示例，展示了：
 * 1. 如何使用 Mockito 模拟依赖
 * 2. 如何组织测试代码结构
 * 3. 如何编写各种场景的测试
 * 4. JUnit 5 和 AssertJ 的使用方法
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 核心注解说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【@DisplayName("...")】
 * - 给测试类/方法起一个易读的名字
 * - 在测试报告中会显示这个名字而不是方法名
 * - 支持中文，让测试报告更易读
 * 
 * 【@Mock】
 * - 创建一个"假的"对象（Mock 对象）
 * - Mock 对象不会真的执行方法，而是返回你设定的值
 * - 用于隔离被测代码，不需要真实的数据库
 * 
 * 【@InjectMocks】
 * - 创建被测试类的实例
 * - 自动将 @Mock 对象注入到这个实例中
 * - 相当于 new UserServiceImpl(mockRepository)
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 测试结构：AAA 模式 / GWT 模式
 * ═══════════════════════════════════════════════════════════════
 * 
 * 每个测试方法都遵循三段式结构：
 * 
 * 【Given / Arrange】准备阶段
 * - 准备测试数据
 * - 设置 Mock 行为
 * 
 * 【When / Act】执行阶段
 * - 调用被测方法
 * 
 * 【Then / Assert】验证阶段
 * - 验证返回值是否正确
 * - 验证 Mock 是否被正确调用
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 Mockito BDD 风格说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * BDD = Behavior Driven Development（行为驱动开发）
 * 
 * 【given(...).willReturn(...)】设置 Mock 行为
 * - given(mock.method()).willReturn(value)
 * - 当调用 mock.method() 时，返回 value
 * 
 * 【then(...).should()】验证 Mock 被调用
 * - then(mock).should().method()
 * - 验证 mock.method() 被调用过
 * 
 * 对比传统 Mockito 写法：
 * <pre>
 * // 传统写法
 * when(mock.method()).thenReturn(value);
 * verify(mock).method();
 * 
 * // BDD 写法（推荐）
 * given(mock.method()).willReturn(value);
 * then(mock).should().method();
 * </pre>
 */
@DisplayName("UserService 单元测试")  // 测试类的显示名称
class UserServiceUnitTest extends BaseUnitTest {

    /**
     * Mock 对象 - 假的 UserRepository
     * 
     * @Mock 注解告诉 Mockito：
     * "创建一个假的 UserRepository，我来控制它的行为"
     */
    @Mock
    private UserRepository userRepository;

    /**
     * 被测对象 - 真实的 UserServiceImpl
     * 
     * @InjectMocks 注解告诉 Mockito：
     * "创建 UserServiceImpl 实例，并把上面的 Mock 对象注入进去"
     */
    @InjectMocks
    private UserServiceImpl userService;

    // 测试数据
    private User testUser;
    private UserDTO testUserDTO;

    /**
     * 每个测试方法执行前运行
     * 用于准备测试数据
     */
    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        // 使用 TestDataFactory 创建测试数据
        testUser = TestDataFactory.createDefaultUser();
        testUserDTO = TestDataFactory.createDefaultUserDTO();
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    创建用户测试                            ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * 【@Nested 注解】
     * 
     * 将相关的测试方法组织在一个内部类中
     * 好处：
     * - 测试报告更清晰
     * - 可以共享设置代码
     * - 逻辑分组更清楚
     */
    @Nested
    @DisplayName("创建用户测试")
    class CreateUserTests {

        /**
         * 【正常流程测试】
         * 
         * 测试命名规范：方法名_场景_预期结果
         * createUser_WithValidData_ShouldReturnCreatedUser
         * = 创建用户_使用有效数据_应该返回创建的用户
         */
        @Test
        @DisplayName("创建用户成功 - 正常流程")
        void createUser_WithValidData_ShouldReturnCreatedUser() {
            // ═══════════ Given：准备测试数据和Mock行为 ═══════════
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            User savedUser = inputDTO.toEntity();
            savedUser.setId(1L);  // 模拟数据库生成的ID

            /*
             * 【设置 Mock 行为】
             * 
             * given(xxx).willReturn(yyy) 的意思是：
             * "当调用 xxx 时，返回 yyy"
             * 
             * any(User.class) 是参数匹配器，表示"任何 User 类型的参数"
             */
            given(userRepository.existsByUsername(inputDTO.getUsername()))
                    .willReturn(false);  // 用户名不存在
            given(userRepository.existsByEmail(inputDTO.getEmail()))
                    .willReturn(false);  // 邮箱不存在
            given(userRepository.save(any(User.class)))
                    .willReturn(savedUser);  // 保存后返回带ID的用户

            // ═══════════ When：执行被测方法 ═══════════
            UserDTO result = userService.createUser(inputDTO);

            // ═══════════ Then：验证结果 ═══════════
            /*
             * 【AssertJ 断言】
             * 
             * assertThat(actual).isXxx(expected) 格式
             * 比 JUnit 原生断言更易读
             * 
             * 例如：
             * - assertThat(result).isNotNull()  // 不为空
             * - assertThat(result.getId()).isEqualTo(1L)  // 等于1
             */
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo(inputDTO.getUsername());
            assertThat(result.getEmail()).isEqualTo(inputDTO.getEmail());

            /*
             * 【验证 Mock 调用】
             * 
             * then(mock).should().method() 验证方法被调用
             * then(mock).should(never()).method() 验证方法从未被调用
             */
            then(userRepository).should().existsByUsername(inputDTO.getUsername());
            then(userRepository).should().existsByEmail(inputDTO.getEmail());
            then(userRepository).should().save(any(User.class));
        }

        /**
         * 【异常场景测试】
         * 
         * 测试当用户名已存在时，应该抛出异常
         */
        @Test
        @DisplayName("创建用户失败 - 用户名已存在")
        void createUser_WithExistingUsername_ShouldThrowException() {
            // Given
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            given(userRepository.existsByUsername(inputDTO.getUsername()))
                    .willReturn(true);  // 用户名已存在！

            // When & Then
            /*
             * 【异常断言】
             * 
             * assertThatThrownBy(() -> xxx)
             *     .isInstanceOf(XxxException.class)
             *     .hasMessageContaining("xxx");
             * 
             * 断言执行 lambda 表达式时会抛出指定类型的异常
             * 并且异常消息包含指定文本
             */
            assertThatThrownBy(() -> userService.createUser(inputDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户名已存在");

            // 验证不应该保存（因为校验就失败了）
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("创建用户失败 - 邮箱已存在")
        void createUser_WithExistingEmail_ShouldThrowException() {
            // Given
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            given(userRepository.existsByUsername(inputDTO.getUsername()))
                    .willReturn(false);  // 用户名不存在
            given(userRepository.existsByEmail(inputDTO.getEmail()))
                    .willReturn(true);   // 但邮箱已存在！

            // When & Then
            assertThatThrownBy(() -> userService.createUser(inputDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("邮箱已存在");
        }
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    查询用户测试                            ║
    // ╚═══════════════════════════════════════════════════════════╝

    @Nested
    @DisplayName("根据ID获取用户测试")
    class GetUserByIdTests {

        @Test
        @DisplayName("根据ID获取用户成功")
        void getUserById_WithExistingId_ShouldReturnUser() {
            // Given
            /*
             * 【Optional.of()】
             * 
             * Optional 是 Java 8 引入的容器类，用于处理可能为空的值
             * - Optional.of(value): 包装一个非空值
             * - Optional.empty(): 空的 Optional
             * - Optional.ofNullable(value): 值可能为空
             */
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            // When
            UserDTO result = userService.getUserById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUser.getId());
            assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("根据ID获取用户失败 - 用户不存在")
        void getUserById_WithNonExistingId_ShouldThrowException() {
            // Given - 返回空的 Optional
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        /**
         * 【参数化测试】
         * 
         * @ParameterizedTest 表示这是一个参数化测试
         * 同一个测试逻辑会用不同的参数执行多次
         * 
         * @ValueSource(longs = {...}) 提供参数值列表
         * 
         * 这个测试会执行4次：
         * - id = 1L
         * - id = 2L
         * - id = 100L
         * - id = 999L
         */
        @ParameterizedTest(name = "测试ID: {0}")  // {0} 是参数占位符
        @ValueSource(longs = {1L, 2L, 100L, 999L})
        @DisplayName("参数化测试 - 多个ID查询")
        void getUserById_WithVariousIds_ShouldBehaveCorrectly(Long id) {
            // Given
            if (id < 100) {
                // ID 小于 100 的认为存在
                User user = TestDataFactory.createDefaultUser();
                user.setId(id);
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                
                // When
                UserDTO result = userService.getUserById(id);
                
                // Then
                assertThat(result.getId()).isEqualTo(id);
            } else {
                // ID >= 100 的认为不存在
                given(userRepository.findById(id)).willReturn(Optional.empty());
                
                // When & Then
                assertThatThrownBy(() -> userService.getUserById(id))
                        .isInstanceOf(ResourceNotFoundException.class);
            }
        }
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    获取所有用户测试                         ║
    // ╚═══════════════════════════════════════════════════════════╝

    @Nested
    @DisplayName("获取所有用户测试")
    class GetAllUsersTests {

        @Test
        @DisplayName("获取所有用户 - 存在多个用户")
        void getAllUsers_WithMultipleUsers_ShouldReturnList() {
            // Given
            List<User> users = TestDataFactory.createRandomUsers(3);
            given(userRepository.findAll()).willReturn(users);

            // When
            List<UserDTO> result = userService.getAllUsers();

            // Then
            assertThat(result).hasSize(3);  // 验证列表大小
        }

        @Test
        @DisplayName("获取所有用户 - 空列表")
        void getAllUsers_WithNoUsers_ShouldReturnEmptyList() {
            // Given
            given(userRepository.findAll()).willReturn(Arrays.asList());

            // When
            List<UserDTO> result = userService.getAllUsers();

            // Then
            assertThat(result).isEmpty();  // 验证列表为空
        }
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                 根据状态获取用户测试                        ║
    // ╚═══════════════════════════════════════════════════════════╝

    @Nested
    @DisplayName("根据状态获取用户测试")
    class GetUsersByStatusTests {

        /**
         * 【枚举参数化测试】
         * 
         * @EnumSource 使用枚举的所有值作为测试参数
         * 这个测试会对每个 UserStatus 值执行一次：
         * - ACTIVE
         * - INACTIVE
         * - SUSPENDED
         */
        @ParameterizedTest(name = "状态: {0}")
        @EnumSource(User.UserStatus.class)
        @DisplayName("参数化测试 - 各种状态查询")
        void getUsersByStatus_WithVariousStatus_ShouldReturnFilteredList(User.UserStatus status) {
            // Given
            User user = TestDataFactory.createUserWithStatus(status);
            given(userRepository.findByStatus(status)).willReturn(Arrays.asList(user));

            // When
            List<UserDTO> result = userService.getUsersByStatus(status);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(status.name());
        }
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    更新用户测试                            ║
    // ╚═══════════════════════════════════════════════════════════╝

    @Nested
    @DisplayName("更新用户测试")
    class UpdateUserTests {

        @Test
        @DisplayName("更新用户成功")
        void updateUser_WithValidData_ShouldReturnUpdatedUser() {
            // Given
            UserDTO updateDTO = TestDataFactory.createUserDTOForUpdate();
            User existingUser = TestDataFactory.createDefaultUser();
            
            given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername(updateDTO.getUsername())).willReturn(false);
            given(userRepository.existsByEmail(updateDTO.getEmail())).willReturn(false);
            /*
             * 【willAnswer 说明】
             * 
             * willAnswer 比 willReturn 更灵活，可以根据输入动态返回结果
             * 
             * invocation.getArgument(0) 获取方法的第一个参数
             * 这里的逻辑是：save 方法返回它接收的参数（模拟数据库保存后返回）
             */
            given(userRepository.save(any(User.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // When
            UserDTO result = userService.updateUser(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(updateDTO.getUsername());
            assertThat(result.getEmail()).isEqualTo(updateDTO.getEmail());
        }

        @Test
        @DisplayName("更新用户失败 - 用户不存在")
        void updateUser_WithNonExistingId_ShouldThrowException() {
            // Given
            UserDTO updateDTO = TestDataFactory.createUserDTOForUpdate();
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(999L, updateDTO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("更新用户失败 - 新用户名已被占用")
        void updateUser_WithExistingUsername_ShouldThrowException() {
            // Given
            UserDTO updateDTO = TestDataFactory.createUserDTOForUpdate();
            User existingUser = TestDataFactory.createDefaultUser();
            
            given(userRepository.findById(1L)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByUsername(updateDTO.getUsername())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(1L, updateDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户名已存在");
        }
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    删除用户测试                            ║
    // ╚═══════════════════════════════════════════════════════════╝

    @Nested
    @DisplayName("删除用户测试")
    class DeleteUserTests {

        @Test
        @DisplayName("删除用户成功")
        void deleteUser_WithExistingId_ShouldDeleteSuccessfully() {
            // Given
            given(userRepository.existsById(1L)).willReturn(true);
            /*
             * 【willDoNothing 说明】
             * 
             * 用于 void 方法（没有返回值的方法）
             * 表示"什么都不做"（因为 Mock 方法默认就什么都不做）
             * 主要作用是明确表达测试意图
             */
            willDoNothing().given(userRepository).deleteById(1L);

            // When
            userService.deleteUser(1L);

            // Then - 验证 deleteById 被调用
            then(userRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("删除用户失败 - 用户不存在")
        void deleteUser_WithNonExistingId_ShouldThrowException() {
            // Given
            given(userRepository.existsById(999L)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            // 验证不应该调用删除方法
            /*
             * 【never() 说明】
             * 
             * 验证方法从未被调用
             * anyLong() 是参数匹配器，匹配任何 Long 类型参数
             */
            then(userRepository).should(never()).deleteById(anyLong());
        }
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    辅助方法测试                            ║
    // ╚═══════════════════════════════════════════════════════════╝

    @Nested
    @DisplayName("辅助方法测试")
    class HelperMethodsTests {

        @Test
        @DisplayName("检查用户名存在 - 存在")
        void existsByUsername_WhenExists_ShouldReturnTrue() {
            given(userRepository.existsByUsername("testuser")).willReturn(true);
            assertThat(userService.existsByUsername("testuser")).isTrue();
        }

        @Test
        @DisplayName("检查用户名存在 - 不存在")
        void existsByUsername_WhenNotExists_ShouldReturnFalse() {
            given(userRepository.existsByUsername("nonexistent")).willReturn(false);
            assertThat(userService.existsByUsername("nonexistent")).isFalse();
        }

        @Test
        @DisplayName("根据状态统计用户数量")
        void countByStatus_ShouldReturnCorrectCount() {
            given(userRepository.countByStatus(User.UserStatus.ACTIVE)).willReturn(10L);
            assertThat(userService.countByStatus(User.UserStatus.ACTIVE)).isEqualTo(10L);
        }
    }
}
