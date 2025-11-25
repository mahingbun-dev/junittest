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
 * UserService 单元测试
 * 
 * 测试颗粒度：单元测试 (Unit Test)
 * 测试目标：UserServiceImpl 业务逻辑
 * 测试策略：使用 Mockito 模拟 Repository 依赖
 * 
 * 命名规范：方法名_测试场景_预期结果
 */
@DisplayName("UserService 单元测试")
class UserServiceUnitTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        testUser = TestDataFactory.createDefaultUser();
        testUserDTO = TestDataFactory.createDefaultUserDTO();
    }

    // ==================== createUser 测试 ====================

    @Nested
    @DisplayName("创建用户测试")
    class CreateUserTests {

        @Test
        @DisplayName("创建用户成功 - 正常流程")
        void createUser_WithValidData_ShouldReturnCreatedUser() {
            // Given - 准备测试数据和Mock行为
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            User savedUser = inputDTO.toEntity();
            savedUser.setId(1L);

            given(userRepository.existsByUsername(inputDTO.getUsername())).willReturn(false);
            given(userRepository.existsByEmail(inputDTO.getEmail())).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(savedUser);

            // When - 执行被测方法
            UserDTO result = userService.createUser(inputDTO);

            // Then - 验证结果
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo(inputDTO.getUsername());
            assertThat(result.getEmail()).isEqualTo(inputDTO.getEmail());

            // 验证 Mock 调用
            then(userRepository).should().existsByUsername(inputDTO.getUsername());
            then(userRepository).should().existsByEmail(inputDTO.getEmail());
            then(userRepository).should().save(any(User.class));
        }

        @Test
        @DisplayName("创建用户失败 - 用户名已存在")
        void createUser_WithExistingUsername_ShouldThrowException() {
            // Given
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            given(userRepository.existsByUsername(inputDTO.getUsername())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(inputDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("用户名已存在");

            // 验证不应该保存
            then(userRepository).should(never()).save(any(User.class));
        }

        @Test
        @DisplayName("创建用户失败 - 邮箱已存在")
        void createUser_WithExistingEmail_ShouldThrowException() {
            // Given
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            given(userRepository.existsByUsername(inputDTO.getUsername())).willReturn(false);
            given(userRepository.existsByEmail(inputDTO.getEmail())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(inputDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("邮箱已存在");
        }
    }

    // ==================== getUserById 测试 ====================

    @Nested
    @DisplayName("根据ID获取用户测试")
    class GetUserByIdTests {

        @Test
        @DisplayName("根据ID获取用户成功")
        void getUserById_WithExistingId_ShouldReturnUser() {
            // Given
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
            // Given
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("用户");
        }

        @ParameterizedTest(name = "测试ID: {0}")
        @ValueSource(longs = {1L, 2L, 100L, 999L})
        @DisplayName("参数化测试 - 多个ID查询")
        void getUserById_WithVariousIds_ShouldBehaveCorrectly(Long id) {
            // Given
            if (id < 100) {
                User user = TestDataFactory.createDefaultUser();
                user.setId(id);
                given(userRepository.findById(id)).willReturn(Optional.of(user));
                
                // When
                UserDTO result = userService.getUserById(id);
                
                // Then
                assertThat(result.getId()).isEqualTo(id);
            } else {
                given(userRepository.findById(id)).willReturn(Optional.empty());
                
                // When & Then
                assertThatThrownBy(() -> userService.getUserById(id))
                        .isInstanceOf(ResourceNotFoundException.class);
            }
        }
    }

    // ==================== getAllUsers 测试 ====================

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
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("获取所有用户 - 空列表")
        void getAllUsers_WithNoUsers_ShouldReturnEmptyList() {
            // Given
            given(userRepository.findAll()).willReturn(Arrays.asList());

            // When
            List<UserDTO> result = userService.getAllUsers();

            // Then
            assertThat(result).isEmpty();
        }
    }

    // ==================== getUsersByStatus 测试 ====================

    @Nested
    @DisplayName("根据状态获取用户测试")
    class GetUsersByStatusTests {

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

    // ==================== updateUser 测试 ====================

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
            given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

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

    // ==================== deleteUser 测试 ====================

    @Nested
    @DisplayName("删除用户测试")
    class DeleteUserTests {

        @Test
        @DisplayName("删除用户成功")
        void deleteUser_WithExistingId_ShouldDeleteSuccessfully() {
            // Given
            given(userRepository.existsById(1L)).willReturn(true);
            willDoNothing().given(userRepository).deleteById(1L);

            // When
            userService.deleteUser(1L);

            // Then
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

            // 验证不应该删除
            then(userRepository).should(never()).deleteById(anyLong());
        }
    }

    // ==================== 辅助方法测试 ====================

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

