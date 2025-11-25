package com.example.repository;

import com.example.base.BaseRepositoryTest;
import com.example.entity.User;
import com.example.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository 数据访问层测试
 * 
 * 测试颗粒度：Repository 测试 (Data Layer Test)
 * 测试目标：UserRepository 的数据访问方法
 * 测试策略：使用 @DataJpaTest + H2 内存数据库
 */
@DisplayName("UserRepository 数据访问层测试")
class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        // 准备测试数据
        User user = TestDataFactory.createUserWithoutId();
        savedUser = entityManager.persistAndFlush(user);
    }

    // ==================== 基本 CRUD 测试 ====================

    @Nested
    @DisplayName("基本 CRUD 操作测试")
    class BasicCrudTests {

        @Test
        @DisplayName("保存用户")
        void save_ShouldPersistUser() {
            // Given
            User newUser = User.builder()
                    .username("newuser")
                    .email("new@example.com")
                    .password("password123")
                    .status(User.UserStatus.ACTIVE)
                    .build();

            // When
            User saved = userRepository.save(newUser);

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCreatedAt()).isNotNull();
            
            // 验证数据库中确实存在
            User found = entityManager.find(User.class, saved.getId());
            assertThat(found).isNotNull();
            assertThat(found.getUsername()).isEqualTo("newuser");
        }

        @Test
        @DisplayName("根据ID查找用户")
        void findById_WithExistingId_ShouldReturnUser() {
            // When
            Optional<User> found = userRepository.findById(savedUser.getId());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo(savedUser.getUsername());
        }

        @Test
        @DisplayName("根据ID查找用户 - 不存在")
        void findById_WithNonExistingId_ShouldReturnEmpty() {
            // When
            Optional<User> found = userRepository.findById(9999L);

            // Then
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("删除用户")
        void delete_ShouldRemoveUser() {
            // When
            userRepository.delete(savedUser);
            entityManager.flush();

            // Then
            User found = entityManager.find(User.class, savedUser.getId());
            assertThat(found).isNull();
        }

        @Test
        @DisplayName("更新用户")
        void update_ShouldModifyUser() {
            // Given
            savedUser.setFullName("Updated Name");

            // When
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            // Then
            User found = entityManager.find(User.class, savedUser.getId());
            assertThat(found.getFullName()).isEqualTo("Updated Name");
        }
    }

    // ==================== 自定义查询方法测试 ====================

    @Nested
    @DisplayName("自定义查询方法测试")
    class CustomQueryTests {

        @Test
        @DisplayName("根据用户名查找")
        void findByUsername_ShouldReturnUser() {
            // When
            Optional<User> found = userRepository.findByUsername(savedUser.getUsername());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("根据邮箱查找")
        void findByEmail_ShouldReturnUser() {
            // When
            Optional<User> found = userRepository.findByEmail(savedUser.getEmail());

            // Then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("检查用户名是否存在 - 存在")
        void existsByUsername_WhenExists_ShouldReturnTrue() {
            // When & Then
            assertThat(userRepository.existsByUsername(savedUser.getUsername())).isTrue();
        }

        @Test
        @DisplayName("检查用户名是否存在 - 不存在")
        void existsByUsername_WhenNotExists_ShouldReturnFalse() {
            // When & Then
            assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
        }

        @Test
        @DisplayName("检查邮箱是否存在")
        void existsByEmail_ShouldReturnCorrectResult() {
            assertThat(userRepository.existsByEmail(savedUser.getEmail())).isTrue();
            assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
        }

        @ParameterizedTest(name = "状态: {0}")
        @EnumSource(User.UserStatus.class)
        @DisplayName("根据状态查找用户列表")
        void findByStatus_ShouldReturnFilteredList(User.UserStatus status) {
            // Given - 创建指定状态的用户
            User user = User.builder()
                    .username("user_" + status.name().toLowerCase())
                    .email(status.name().toLowerCase() + "@example.com")
                    .password("password123")
                    .status(status)
                    .build();
            entityManager.persistAndFlush(user);

            // When
            List<User> users = userRepository.findByStatus(status);

            // Then
            assertThat(users).isNotEmpty();
            assertThat(users).allMatch(u -> u.getStatus() == status);
        }

        @Test
        @DisplayName("根据用户名模糊查询")
        void findByUsernameContaining_ShouldReturnMatchingUsers() {
            // Given
            User user1 = User.builder()
                    .username("john_doe")
                    .email("john@example.com")
                    .password("password")
                    .status(User.UserStatus.ACTIVE)
                    .build();
            User user2 = User.builder()
                    .username("jane_doe")
                    .email("jane@example.com")
                    .password("password")
                    .status(User.UserStatus.ACTIVE)
                    .build();
            entityManager.persistAndFlush(user1);
            entityManager.persistAndFlush(user2);

            // When
            List<User> users = userRepository.findByUsernameContaining("doe");

            // Then
            assertThat(users).hasSize(2);
        }
    }

    // ==================== JPQL 查询测试 ====================

    @Nested
    @DisplayName("JPQL 和原生 SQL 查询测试")
    class JpqlAndNativeQueryTests {

        @Test
        @DisplayName("JPQL - 根据状态统计用户数量")
        void countByStatus_ShouldReturnCorrectCount() {
            // Given - savedUser 的状态是 ACTIVE
            User inactiveUser = User.builder()
                    .username("inactive_user")
                    .email("inactive@example.com")
                    .password("password")
                    .status(User.UserStatus.INACTIVE)
                    .build();
            entityManager.persistAndFlush(inactiveUser);

            // When
            long activeCount = userRepository.countByStatus(User.UserStatus.ACTIVE);
            long inactiveCount = userRepository.countByStatus(User.UserStatus.INACTIVE);

            // Then
            assertThat(activeCount).isGreaterThanOrEqualTo(1);
            assertThat(inactiveCount).isEqualTo(1);
        }

        @Test
        @DisplayName("原生 SQL - 根据邮箱域名查询")
        void findByEmailDomain_ShouldReturnMatchingUsers() {
            // Given
            User gmailUser = User.builder()
                    .username("gmail_user")
                    .email("user@gmail.com")
                    .password("password")
                    .status(User.UserStatus.ACTIVE)
                    .build();
            entityManager.persistAndFlush(gmailUser);

            // When
            List<User> gmailUsers = userRepository.findByEmailDomain("gmail.com");

            // Then
            assertThat(gmailUsers).hasSize(1);
            assertThat(gmailUsers.get(0).getEmail()).contains("gmail.com");
        }
    }

    // ==================== 边界条件测试 ====================

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("查询所有用户 - 空表")
        void findAll_WithEmptyTable_ShouldReturnEmptyList() {
            // Given
            userRepository.deleteAll();
            entityManager.flush();

            // When
            List<User> users = userRepository.findAll();

            // Then
            assertThat(users).isEmpty();
        }

        @Test
        @DisplayName("保存用户 - 用户名最大长度")
        void save_WithMaxLengthUsername_ShouldSucceed() {
            // Given
            String maxLengthUsername = "a".repeat(50); // 假设最大长度是50
            User user = User.builder()
                    .username(maxLengthUsername)
                    .email("maxlength@example.com")
                    .password("password123")
                    .status(User.UserStatus.ACTIVE)
                    .build();

            // When
            User saved = userRepository.save(user);
            entityManager.flush();

            // Then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getUsername()).hasSize(50);
        }
    }
}

