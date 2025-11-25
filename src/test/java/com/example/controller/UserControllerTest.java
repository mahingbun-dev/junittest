package com.example.controller;

import com.example.base.BaseWebMvcTest;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.exception.ResourceNotFoundException;
import com.example.service.UserService;
import com.example.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 控制器层测试
 * 
 * 测试颗粒度：Controller 测试 (Web Layer Test)
 * 测试目标：UserController 的 HTTP 接口
 * 测试策略：使用 @WebMvcTest + MockMvc，Mock Service 层
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController 控制器层测试")
class UserControllerTest extends BaseWebMvcTest {

    @MockBean
    private UserService userService;

    private UserDTO testUserDTO;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        testUserDTO = TestDataFactory.createDefaultUserDTO();
    }

    // ==================== POST /api/users - 创建用户 ====================

    @Nested
    @DisplayName("POST /api/users - 创建用户")
    class CreateUserTests {

        @Test
        @DisplayName("创建用户成功 - 返回 201")
        void createUser_WithValidData_ShouldReturn201() throws Exception {
            // Given
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            UserDTO savedDTO = TestDataFactory.createDefaultUserDTO();
            given(userService.createUser(any(UserDTO.class))).willReturn(savedDTO);

            // When & Then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(inputDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("用户创建成功"))
                    .andExpect(jsonPath("$.data.id").value(savedDTO.getId()))
                    .andExpect(jsonPath("$.data.username").value(savedDTO.getUsername()))
                    .andExpect(jsonPath("$.data.email").value(savedDTO.getEmail()));
        }

        @Test
        @DisplayName("创建用户失败 - 参数校验失败返回 400")
        void createUser_WithInvalidData_ShouldReturn400() throws Exception {
            // Given
            UserDTO invalidDTO = TestDataFactory.createInvalidUserDTO();

            // When & Then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(invalidDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.data").isMap());
        }

        @Test
        @DisplayName("创建用户失败 - 用户名已存在返回 400")
        void createUser_WithExistingUsername_ShouldReturn400() throws Exception {
            // Given
            UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
            given(userService.createUser(any(UserDTO.class)))
                    .willThrow(new BusinessException("用户名已存在"));

            // When & Then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(inputDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").containsString("用户名已存在"));
        }
    }

    // ==================== GET /api/users/{id} - 根据ID获取用户 ====================

    @Nested
    @DisplayName("GET /api/users/{id} - 根据ID获取用户")
    class GetUserByIdTests {

        @Test
        @DisplayName("获取用户成功 - 返回 200")
        void getUserById_WithExistingId_ShouldReturn200() throws Exception {
            // Given
            given(userService.getUserById(1L)).willReturn(testUserDTO);

            // When & Then
            mockMvc.perform(get("/api/users/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(testUserDTO.getId()))
                    .andExpect(jsonPath("$.data.username").value(testUserDTO.getUsername()));
        }

        @Test
        @DisplayName("获取用户失败 - 用户不存在返回 404")
        void getUserById_WithNonExistingId_ShouldReturn404() throws Exception {
            // Given
            given(userService.getUserById(999L))
                    .willThrow(new ResourceNotFoundException("用户", "id", 999L));

            // When & Then
            mockMvc.perform(get("/api/users/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    // ==================== GET /api/users - 获取所有用户 ====================

    @Nested
    @DisplayName("GET /api/users - 获取所有用户")
    class GetAllUsersTests {

        @Test
        @DisplayName("获取所有用户 - 存在多个用户")
        void getAllUsers_WithMultipleUsers_ShouldReturnList() throws Exception {
            // Given
            List<UserDTO> users = TestDataFactory.createRandomUserDTOs(3);
            given(userService.getAllUsers()).willReturn(users);

            // When & Then
            mockMvc.perform(get("/api/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(3)));
        }

        @Test
        @DisplayName("获取所有用户 - 空列表")
        void getAllUsers_WithNoUsers_ShouldReturnEmptyList() throws Exception {
            // Given
            given(userService.getAllUsers()).willReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }

    // ==================== GET /api/users/status/{status} - 根据状态获取用户 ====================

    @Nested
    @DisplayName("GET /api/users/status/{status} - 根据状态获取用户")
    class GetUsersByStatusTests {

        @Test
        @DisplayName("根据状态获取用户列表")
        void getUsersByStatus_ShouldReturnFilteredList() throws Exception {
            // Given
            List<UserDTO> activeUsers = Arrays.asList(testUserDTO);
            given(userService.getUsersByStatus(User.UserStatus.ACTIVE)).willReturn(activeUsers);

            // When & Then
            mockMvc.perform(get("/api/users/status/{status}", "ACTIVE"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
        }
    }

    // ==================== PUT /api/users/{id} - 更新用户 ====================

    @Nested
    @DisplayName("PUT /api/users/{id} - 更新用户")
    class UpdateUserTests {

        @Test
        @DisplayName("更新用户成功 - 返回 200")
        void updateUser_WithValidData_ShouldReturn200() throws Exception {
            // Given
            UserDTO updateDTO = TestDataFactory.createUserDTOForUpdate();
            UserDTO updatedDTO = TestDataFactory.createDefaultUserDTO();
            updatedDTO.setUsername(updateDTO.getUsername());
            
            given(userService.updateUser(eq(1L), any(UserDTO.class))).willReturn(updatedDTO);

            // When & Then
            mockMvc.perform(put("/api/users/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(updateDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("用户更新成功"));
        }

        @Test
        @DisplayName("更新用户失败 - 用户不存在返回 404")
        void updateUser_WithNonExistingId_ShouldReturn404() throws Exception {
            // Given
            UserDTO updateDTO = TestDataFactory.createUserDTOForUpdate();
            given(userService.updateUser(eq(999L), any(UserDTO.class)))
                    .willThrow(new ResourceNotFoundException("用户", "id", 999L));

            // When & Then
            mockMvc.perform(put("/api/users/{id}", 999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(updateDTO)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    // ==================== DELETE /api/users/{id} - 删除用户 ====================

    @Nested
    @DisplayName("DELETE /api/users/{id} - 删除用户")
    class DeleteUserTests {

        @Test
        @DisplayName("删除用户成功 - 返回 200")
        void deleteUser_WithExistingId_ShouldReturn200() throws Exception {
            // Given
            willDoNothing().given(userService).deleteUser(1L);

            // When & Then
            mockMvc.perform(delete("/api/users/{id}", 1L))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("用户删除成功"));
        }

        @Test
        @DisplayName("删除用户失败 - 用户不存在返回 404")
        void deleteUser_WithNonExistingId_ShouldReturn404() throws Exception {
            // Given
            willThrow(new ResourceNotFoundException("用户", "id", 999L))
                    .given(userService).deleteUser(999L);

            // When & Then
            mockMvc.perform(delete("/api/users/{id}", 999L))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    // ==================== GET /api/users/exists/username/{username} ====================

    @Nested
    @DisplayName("GET /api/users/exists/username/{username} - 检查用户名是否存在")
    class ExistsByUsernameTests {

        @Test
        @DisplayName("用户名存在")
        void existsByUsername_WhenExists_ShouldReturnTrue() throws Exception {
            // Given
            given(userService.existsByUsername("testuser")).willReturn(true);

            // When & Then
            mockMvc.perform(get("/api/users/exists/username/{username}", "testuser"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        @DisplayName("用户名不存在")
        void existsByUsername_WhenNotExists_ShouldReturnFalse() throws Exception {
            // Given
            given(userService.existsByUsername("nonexistent")).willReturn(false);

            // When & Then
            mockMvc.perform(get("/api/users/exists/username/{username}", "nonexistent"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(false));
        }
    }

    // ==================== GET /api/users/count/status/{status} ====================

    @Nested
    @DisplayName("GET /api/users/count/status/{status} - 根据状态统计用户数量")
    class CountByStatusTests {

        @Test
        @DisplayName("统计活跃用户数量")
        void countByStatus_ShouldReturnCount() throws Exception {
            // Given
            given(userService.countByStatus(User.UserStatus.ACTIVE)).willReturn(10L);

            // When & Then
            mockMvc.perform(get("/api/users/count/status/{status}", "ACTIVE"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(10));
        }
    }
}

