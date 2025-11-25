package com.example.manage.controller;

import com.example.test.base.BaseControllerTest;
import com.example.test.util.TestDataFactory;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Manage 服务 - 用户管理控制器测试示例
 * 
 * 测试颗粒度：Controller 层测试
 * 测试目标：UserManageController HTTP 接口
 * 
 * 使用方法：
 * 1. 修改 @WebMvcTest 为你的实际 Controller 类
 * 2. 添加 @MockBean 注解模拟你的 Service
 * 3. 编写各接口的测试用例
 */
// TODO: 取消注释并替换为你的实际 Controller 类
// @WebMvcTest(UserManageController.class)
@DisplayName("【Manage服务】用户管理控制器测试")
class UserManageControllerTest extends BaseControllerTest {

    // TODO: 添加 Service Mock
    // @MockBean
    // private UserManageService userManageService;

    private Long testUserId;
    private String testUsername;
    private String testEmail;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        testUserId = TestDataFactory.randomId();
        testUsername = TestDataFactory.randomUsername();
        testEmail = TestDataFactory.randomEmail();
    }

    // ==================== POST /manage/api/users ====================

    @Nested
    @DisplayName("POST /manage/api/users - 创建用户")
    class CreateUserTests {

        // @Test
        @DisplayName("创建用户成功 - 返回 201")
        void createUser_WithValidData_ShouldReturn201() throws Exception {
            // Given
            Map<String, Object> request = new HashMap<>();
            request.put("username", testUsername);
            request.put("email", testEmail);
            request.put("password", "password123");

            // Mock Service 返回
            // UserDTO savedUser = UserDTO.builder()
            //         .id(testUserId)
            //         .username(testUsername)
            //         .email(testEmail)
            //         .build();
            // given(userManageService.createUser(any())).willReturn(savedUser);

            // When & Then
            // mockMvc.perform(post("/manage/api/users")
            //                 .contentType(MediaType.APPLICATION_JSON)
            //                 .content(toJson(request)))
            //         .andDo(print())
            //         .andExpect(status().isCreated())
            //         .andExpect(jsonPath("$.code").value(200))
            //         .andExpect(jsonPath("$.data.id").value(testUserId))
            //         .andExpect(jsonPath("$.data.username").value(testUsername));
        }

        // @Test
        @DisplayName("创建用户失败 - 参数校验失败返回 400")
        void createUser_WithInvalidData_ShouldReturn400() throws Exception {
            // Given - 无效请求数据
            Map<String, Object> request = new HashMap<>();
            request.put("username", ""); // 空用户名
            request.put("email", "invalid"); // 无效邮箱

            // When & Then
            // mockMvc.perform(post("/manage/api/users")
            //                 .contentType(MediaType.APPLICATION_JSON)
            //                 .content(toJson(request)))
            //         .andDo(print())
            //         .andExpect(status().isBadRequest())
            //         .andExpect(jsonPath("$.code").value(400));
        }
    }

    // ==================== GET /manage/api/users/{id} ====================

    @Nested
    @DisplayName("GET /manage/api/users/{id} - 获取用户")
    class GetUserTests {

        // @Test
        @DisplayName("获取用户成功 - 返回 200")
        void getUserById_WithExistingId_ShouldReturn200() throws Exception {
            // Given
            // UserDTO user = UserDTO.builder()
            //         .id(testUserId)
            //         .username(testUsername)
            //         .email(testEmail)
            //         .build();
            // given(userManageService.getUserById(testUserId)).willReturn(user);

            // When & Then
            // mockMvc.perform(get("/manage/api/users/{id}", testUserId))
            //         .andDo(print())
            //         .andExpect(status().isOk())
            //         .andExpect(jsonPath("$.data.id").value(testUserId))
            //         .andExpect(jsonPath("$.data.username").value(testUsername));
        }

        // @Test
        @DisplayName("获取用户失败 - 用户不存在返回 404")
        void getUserById_WithNonExistingId_ShouldReturn404() throws Exception {
            // Given
            // given(userManageService.getUserById(999L))
            //         .willThrow(new ResourceNotFoundException("用户", "id", 999L));

            // When & Then
            // mockMvc.perform(get("/manage/api/users/{id}", 999L))
            //         .andDo(print())
            //         .andExpect(status().isNotFound());
        }
    }

    // ==================== PUT /manage/api/users/{id} ====================

    @Nested
    @DisplayName("PUT /manage/api/users/{id} - 更新用户")
    class UpdateUserTests {

        // @Test
        @DisplayName("更新用户成功 - 返回 200")
        void updateUser_WithValidData_ShouldReturn200() throws Exception {
            // 实现更新用户测试
        }
    }

    // ==================== DELETE /manage/api/users/{id} ====================

    @Nested
    @DisplayName("DELETE /manage/api/users/{id} - 删除用户")
    class DeleteUserTests {

        // @Test
        @DisplayName("删除用户成功 - 返回 200")
        void deleteUser_WithExistingId_ShouldReturn200() throws Exception {
            // 实现删除用户测试
        }
    }

    // ==================== 权限相关接口测试 ====================

    @Nested
    @DisplayName("权限管理接口测试")
    class PermissionApiTests {

        // @Test
        @DisplayName("POST /manage/api/users/{id}/roles - 分配角色")
        void assignRole_ShouldReturn200() throws Exception {
            // 实现角色分配测试
        }

        // @Test
        @DisplayName("GET /manage/api/permissions/check - 检查权限")
        void checkPermission_ShouldReturnCorrectResult() throws Exception {
            // 实现权限检查测试
        }
    }
}

