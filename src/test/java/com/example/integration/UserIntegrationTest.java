package com.example.integration;

import com.example.base.BaseIntegrationTest;
import com.example.dto.ApiResponse;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.util.TestDataFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户模块集成测试
 * 
 * 测试颗粒度：集成测试 (Integration Test)
 * 测试目标：整个用户模块的端到端流程
 * 测试策略：启动完整 Spring 容器，使用真实数据库
 * 
 * 注意：集成测试类名需要以 IntegrationTest 或 IT 结尾
 */
@AutoConfigureMockMvc
@DisplayName("用户模块集成测试")
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        // 清理测试数据
        userRepository.deleteAll();
    }

    // ==================== 完整 CRUD 流程测试 ====================

    @Nested
    @DisplayName("完整 CRUD 流程测试")
    class FullCrudFlowTests {

        @Test
        @DisplayName("完整的用户 CRUD 流程")
        void fullUserCrudFlow() throws Exception {
            // 1. 创建用户
            UserDTO createDTO = UserDTO.builder()
                    .username("integrationuser")
                    .email("integration@example.com")
                    .password("password123")
                    .fullName("Integration Test User")
                    .build();

            MvcResult createResult = mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("integrationuser"))
                    .andReturn();

            // 提取创建的用户ID
            String responseJson = createResult.getResponse().getContentAsString();
            ApiResponse<UserDTO> createResponse = objectMapper.readValue(responseJson, 
                    new TypeReference<ApiResponse<UserDTO>>() {});
            Long userId = createResponse.getData().getId();
            assertThat(userId).isNotNull();

            // 2. 读取用户
            mockMvc.perform(get("/api/users/{id}", userId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(userId))
                    .andExpect(jsonPath("$.data.username").value("integrationuser"));

            // 3. 更新用户
            UserDTO updateDTO = UserDTO.builder()
                    .fullName("Updated Integration User")
                    .build();

            mockMvc.perform(put("/api/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.fullName").value("Updated Integration User"));

            // 4. 验证更新后的数据
            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(jsonPath("$.data.fullName").value("Updated Integration User"));

            // 5. 删除用户
            mockMvc.perform(delete("/api/users/{id}", userId))
                    .andDo(print())
                    .andExpect(status().isOk());

            // 6. 验证删除成功
            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(status().isNotFound());
        }
    }

    // ==================== 业务场景测试 ====================

    @Nested
    @DisplayName("业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("场景：批量创建用户并按状态查询")
        void scenario_CreateUsersAndQueryByStatus() throws Exception {
            // 创建多个不同状态的用户
            for (int i = 0; i < 3; i++) {
                UserDTO dto = UserDTO.builder()
                        .username("activeuser" + i)
                        .email("active" + i + "@example.com")
                        .password("password123")
                        .build();
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated());
            }

            // 查询所有用户
            mockMvc.perform(get("/api/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(3));

            // 按状态查询（默认是 ACTIVE）
            mockMvc.perform(get("/api/users/status/ACTIVE"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(3));
        }

        @Test
        @DisplayName("场景：用户名唯一性校验")
        void scenario_UsernameUniquenessValidation() throws Exception {
            // 创建第一个用户
            UserDTO firstUser = UserDTO.builder()
                    .username("uniqueuser")
                    .email("first@example.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(firstUser)))
                    .andExpect(status().isCreated());

            // 尝试创建同名用户
            UserDTO duplicateUser = UserDTO.builder()
                    .username("uniqueuser")
                    .email("second@example.com")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateUser)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("用户名已存在")));
        }

        @Test
        @DisplayName("场景：根据用户名查询用户")
        void scenario_QueryByUsername() throws Exception {
            // 创建用户
            UserDTO dto = UserDTO.builder()
                    .username("queryuser")
                    .email("query@example.com")
                    .password("password123")
                    .fullName("Query User")
                    .build();

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());

            // 根据用户名查询
            mockMvc.perform(get("/api/users/username/{username}", "queryuser"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.username").value("queryuser"))
                    .andExpect(jsonPath("$.data.fullName").value("Query User"));
        }
    }

    // ==================== 数据一致性测试 ====================

    @Nested
    @DisplayName("数据一致性测试")
    class DataConsistencyTests {

        @Test
        @DisplayName("验证创建用户后数据库数据正确")
        void createUser_ShouldPersistCorrectDataToDatabase() throws Exception {
            // 创建用户
            UserDTO dto = UserDTO.builder()
                    .username("dbuser")
                    .email("db@example.com")
                    .password("password123")
                    .fullName("DB User")
                    .build();

            MvcResult result = mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            // 直接从数据库验证
            List<User> users = userRepository.findAll();
            assertThat(users).hasSize(1);
            
            User savedUser = users.get(0);
            assertThat(savedUser.getUsername()).isEqualTo("dbuser");
            assertThat(savedUser.getEmail()).isEqualTo("db@example.com");
            assertThat(savedUser.getFullName()).isEqualTo("DB User");
            assertThat(savedUser.getStatus()).isEqualTo(User.UserStatus.ACTIVE);
            assertThat(savedUser.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("验证删除用户后数据库数据被清除")
        void deleteUser_ShouldRemoveDataFromDatabase() throws Exception {
            // 先创建用户
            User user = User.builder()
                    .username("tobeDeleted")
                    .email("delete@example.com")
                    .password("password123")
                    .status(User.UserStatus.ACTIVE)
                    .build();
            User savedUser = userRepository.save(user);
            Long userId = savedUser.getId();

            // 验证用户存在
            assertThat(userRepository.existsById(userId)).isTrue();

            // 删除用户
            mockMvc.perform(delete("/api/users/{id}", userId))
                    .andExpect(status().isOk());

            // 验证数据库中已删除
            assertThat(userRepository.existsById(userId)).isFalse();
        }
    }

    // ==================== 并发测试 ====================

    @Nested
    @DisplayName("并发测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("统计用户数量")
        void countByStatus_ShouldReturnCorrectCount() throws Exception {
            // 创建多个用户
            for (int i = 0; i < 5; i++) {
                User user = User.builder()
                        .username("countuser" + i)
                        .email("count" + i + "@example.com")
                        .password("password123")
                        .status(User.UserStatus.ACTIVE)
                        .build();
                userRepository.save(user);
            }

            // 验证统计
            mockMvc.perform(get("/api/users/count/status/ACTIVE"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(5));
        }
    }
}

