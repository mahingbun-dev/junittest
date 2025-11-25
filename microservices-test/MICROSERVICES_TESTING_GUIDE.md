# 微服务单元测试框架使用指南

本文档详细说明如何在 Spring Boot 微服务架构（manage、API、job、query 四个服务）中集成和使用此测试框架。

## 目录

1. [框架概述](#1-框架概述)
2. [快速集成](#2-快速集成)
3. [各微服务测试策略](#3-各微服务测试策略)
4. [测试颗粒度详解](#4-测试颗粒度详解)
5. [微服务间调用测试](#5-微服务间调用测试)
6. [最佳实践](#6-最佳实践)

---

## 1. 框架概述

### 1.1 测试框架架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     微服务测试框架架构                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    common-test 公共模块                   │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │  • 测试基类 (BaseUnitTest, BaseIntegrationTest, ...)     │   │
│  │  • 工具类 (TestDataFactory, JsonTestUtil, ...)           │   │
│  │  • Mock 服务 (ManageServiceMock, ApiServiceMock, ...)    │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│         ┌────────────────────┼────────────────────┐            │
│         │                    │                    │            │
│         ▼                    ▼                    ▼            │
│  ┌────────────┐      ┌────────────┐      ┌────────────┐       │
│  │  manage    │      │    api     │      │    job     │       │
│  │  service   │      │  service   │      │  service   │       │
│  │   test     │      │   test     │      │   test     │       │
│  └────────────┘      └────────────┘      └────────────┘       │
│         │                    │                    │            │
│         │                    ▼                    │            │
│         │            ┌────────────┐               │            │
│         │            │   query    │               │            │
│         └───────────►│  service   │◄──────────────┘            │
│                      │   test     │                            │
│                      └────────────┘                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 四个微服务的职责

| 微服务 | 职责 | 主要测试类型 |
|--------|------|-------------|
| **manage** | 用户管理、权限管理、配置管理 | Service 单元测试、集成测试 |
| **api** | API 网关、认证授权、请求路由 | Controller 测试、微服务调用测试 |
| **job** | 定时任务、异步任务调度 | 任务执行测试、异步测试 |
| **query** | 数据查询、报表统计 | Repository 测试、缓存测试 |

---

## 2. 快速集成

### 2.1 添加 common-test 依赖

在各微服务的 `pom.xml` 中添加公共测试模块依赖：

```xml
<dependency>
    <groupId>com.example.test</groupId>
    <artifactId>common-test</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

### 2.2 创建测试目录结构

每个微服务的测试目录结构建议如下：

```
src/test/java/com/example/{service}/
├── unit/                      # 单元测试
│   ├── service/              # Service 层单元测试
│   ├── entity/               # 实体类测试
│   └── dto/                  # DTO 测试
├── repository/               # Repository 测试
├── controller/               # Controller 测试
├── integration/              # 集成测试
└── ApplicationTests.java     # 启动测试
```

### 2.3 创建测试配置文件

在 `src/test/resources/application-test.yml` 中配置测试环境：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

# 其他微服务地址（用于 WireMock 模拟）
external:
  manage-service:
    url: http://localhost:${wiremock.port}/manage
  api-service:
    url: http://localhost:${wiremock.port}/api
  job-service:
    url: http://localhost:${wiremock.port}/job
  query-service:
    url: http://localhost:${wiremock.port}/query

logging:
  level:
    root: WARN
    com.example: INFO
```

---

## 3. 各微服务测试策略

### 3.1 Manage 服务测试策略

**主要测试重点**：
- 用户 CRUD 操作
- 权限验证逻辑
- 配置管理功能

**测试用例示例**：

```java
@DisplayName("【Manage服务】用户管理服务单元测试")
class UserManageServiceTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManageServiceImpl userManageService;

    @Test
    @DisplayName("创建用户成功")
    void createUser_WithValidData_ShouldSucceed() {
        // Given
        UserDTO inputDTO = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
        
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any())).willAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        // When
        UserDTO result = userManageService.createUser(inputDTO);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        then(userRepository).should().save(any());
    }
}
```

### 3.2 API 服务测试策略

**主要测试重点**：
- 认证授权流程
- 请求路由
- 限流和熔断
- 微服务调用

**测试用例示例**：

```java
@DisplayName("【API服务】认证控制器测试")
@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("登录成功返回 Token")
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Given
        Map<String, String> request = Map.of(
            "username", "testuser",
            "password", "password123"
        );
        
        TokenDTO tokenDTO = TokenDTO.builder()
                .accessToken("test-token")
                .expiresIn(7200)
                .build();
        given(authService.login(anyString(), anyString())).willReturn(tokenDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("test-token"));
    }
}
```

**微服务调用测试**：

```java
@DisplayName("【API服务】API网关集成测试")
class ApiGatewayIntegrationTest extends BaseMicroserviceTest {

    @Test
    @DisplayName("调用 Manage 服务获取用户信息")
    void getUser_ThroughGateway_ShouldReturnUserFromManageService() throws Exception {
        // Given - Mock Manage 服务响应
        ManageServiceMock.mockGetUser(1L, "testuser", "test@example.com");

        // When & Then
        mockMvc.perform(get("/api/v1/users/1")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
}
```

### 3.3 Job 服务测试策略

**主要测试重点**：
- 定时任务调度
- 异步任务执行
- 任务状态管理
- 失败重试机制

**测试用例示例**：

```java
@DisplayName("【Job服务】数据同步任务测试")
class DataSyncJobTest extends BaseUnitTest {

    @Mock
    private DataSourceService dataSourceService;
    
    @Mock
    private DataTargetService dataTargetService;

    @InjectMocks
    private DataSyncJob dataSyncJob;

    @Test
    @DisplayName("同步任务执行成功")
    void execute_WithData_ShouldSyncSuccessfully() {
        // Given
        List<DataRecord> records = Arrays.asList(
                new DataRecord(1L, "data1"),
                new DataRecord(2L, "data2")
        );
        given(dataSourceService.fetchData(any())).willReturn(records);
        given(dataTargetService.saveData(any())).willReturn(true);

        // When
        JobResult result = dataSyncJob.execute();

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getProcessedCount()).isEqualTo(2);
    }
}
```

**异步任务测试**：

```java
@DisplayName("【Job服务】定时任务集成测试")
class ScheduledJobIntegrationTest extends BaseJobTest {

    @Test
    @DisplayName("异步任务执行并等待完成")
    void asyncJob_ShouldCompleteEventually() throws Exception {
        // 触发任务
        String jobId = jobService.triggerJob("dataSync");
        
        // 使用 Awaitility 等待任务完成
        await()
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .until(() -> isJobCompleted(jobId));
        
        // 验证结果
        JobExecution execution = jobExecutionRepository.findById(jobId).get();
        assertThat(execution.getStatus()).isEqualTo(JobStatus.COMPLETED);
    }
}
```

### 3.4 Query 服务测试策略

**主要测试重点**：
- 分页查询
- 条件查询
- 聚合统计
- 缓存策略

**测试用例示例**：

```java
@DisplayName("【Query服务】查询服务单元测试")
class QueryServiceTest extends BaseUnitTest {

    @Mock
    private UserQueryRepository userQueryRepository;
    
    @Mock
    private CacheService cacheService;

    @InjectMocks
    private QueryServiceImpl queryService;

    @Test
    @DisplayName("分页查询返回正确结果")
    void pageQuery_ShouldReturnPagedResult() {
        // Given
        List<UserVO> users = Arrays.asList(
                UserVO.builder().id(1L).username("user1").build(),
                UserVO.builder().id(2L).username("user2").build()
        );
        Page<UserVO> page = new PageImpl<>(users, PageRequest.of(0, 10), 100);
        given(userQueryRepository.findAll(any(Pageable.class))).willReturn(page);

        // When
        PageResult<UserVO> result = queryService.queryUsers(0, 10);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(100);
    }

    @Test
    @DisplayName("查询命中缓存")
    void query_WhenCacheHit_ShouldReturnCachedResult() {
        // Given
        UserVO cachedUser = UserVO.builder().id(1L).username("cached").build();
        given(cacheService.get("user:1")).willReturn(cachedUser);

        // When
        UserVO result = queryService.getUserById(1L);

        // Then
        assertThat(result.getUsername()).isEqualTo("cached");
        then(userQueryRepository).should(never()).findById(anyLong());
    }
}
```

---

## 4. 测试颗粒度详解

### 4.1 测试金字塔

```
                    /\
                   /  \
                  / E2E \                    ← 端到端测试 (最少)
                 /──────\
                /  微服务 \                  ← 微服务集成测试
               /  集成测试 \
              /────────────\
             /   Controller  \              ← Controller 测试
            /      测试       \
           /──────────────────\
          /    Repository      \            ← Repository 测试
         /       测试          \
        /────────────────────────\
       /     Service 单元测试     \         ← 单元测试 (最多)
      /──────────────────────────\
     /    Entity/DTO 单元测试     \
    /────────────────────────────────\
```

### 4.2 各层测试对比

| 测试类型 | 基类 | 启动 Spring | 数据库 | 外部服务 | 速度 |
|---------|------|------------|--------|---------|------|
| 单元测试 | `BaseUnitTest` | ❌ | ❌ | ❌ | 最快 |
| Repository | `BaseRepositoryTest` | ⚡部分 | ✅ H2 | ❌ | 快 |
| Controller | `BaseControllerTest` | ⚡部分 | ❌ | ❌ | 快 |
| 集成测试 | `BaseIntegrationTest` | ✅ | ✅ H2 | ❌ | 较慢 |
| 微服务测试 | `BaseMicroserviceTest` | ✅ | ✅ H2 | ✅ WireMock | 较慢 |
| 任务测试 | `BaseJobTest` | ✅ | ✅ H2 | ❌ | 较慢 |

---

## 5. 微服务间调用测试

### 5.1 使用 WireMock 模拟其他服务

```java
// 在测试中模拟 Manage 服务
ManageServiceMock.mockGetUser(1L, "testuser", "test@example.com");
ManageServiceMock.mockUserNotFound(999L);
ManageServiceMock.mockCheckPermission(1L, "admin", true);

// 模拟 API 服务
ApiServiceMock.mockValidateTokenSuccess("token", 1L, "testuser");
ApiServiceMock.mockLoginSuccess("user", "pass", "new-token");

// 模拟 Query 服务
QueryServiceMock.mockPageQuery("users", userList, 100, 0, 10);
QueryServiceMock.mockStatistics("users", statisticsMap);

// 模拟 Job 服务
JobServiceMock.mockTriggerJob("dataSync", "job-123");
JobServiceMock.mockGetJobStatus("job-123", "COMPLETED");
```

### 5.2 服务降级测试

```java
@Test
@DisplayName("服务超时触发降级")
void serviceTimeout_ShouldTriggerFallback() throws Exception {
    // Mock 超时响应
    MockServiceHelper.mockTimeout("/manage/api/users/1", 5000);

    // 执行请求，验证降级响应
    mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fallback").value(true));
}

@Test
@DisplayName("服务错误触发熔断")
void serviceError_ShouldTriggerCircuitBreaker() throws Exception {
    // Mock 服务错误
    MockServiceHelper.mockServerError("/manage/api/users/1");

    // 多次请求触发熔断
    for (int i = 0; i < 10; i++) {
        mockMvc.perform(get("/api/v1/users/1"));
    }

    // 验证熔断器状态
    assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
}
```

---

## 6. 最佳实践

### 6.1 测试命名规范

```
方法名_测试场景_预期结果

示例：
- createUser_WithValidData_ShouldReturnCreatedUser
- login_WithInvalidCredentials_ShouldReturn401
- executeJob_WhenAlreadyRunning_ShouldSkip
- pageQuery_WithEmptyResult_ShouldReturnEmptyPage
```

### 6.2 测试结构 (AAA/GWT 模式)

```java
@Test
void testMethod() {
    // Arrange / Given - 准备测试数据和 Mock
    UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
    given(userRepository.save(any())).willReturn(savedUser);

    // Act / When - 执行被测方法
    UserDTO result = userService.createUser(inputDTO);

    // Assert / Then - 验证结果
    assertThat(result).isNotNull();
    then(userRepository).should().save(any());
}
```

### 6.3 使用 @Nested 组织测试

```java
@DisplayName("用户服务测试")
class UserServiceTest {

    @Nested
    @DisplayName("创建用户")
    class CreateUserTests {
        @Test void withValidData_ShouldSucceed() { }
        @Test void withDuplicateUsername_ShouldFail() { }
    }

    @Nested
    @DisplayName("查询用户")
    class GetUserTests {
        @Test void withExistingId_ShouldReturnUser() { }
        @Test void withNonExistingId_ShouldThrowException() { }
    }
}
```

### 6.4 测试数据管理

```java
// 使用 TestDataFactory 生成测试数据
Long userId = TestDataFactory.randomId();
String username = TestDataFactory.randomUsername();
String email = TestDataFactory.randomEmail();

// 生成完整对象
UserDTO userDTO = UserDTO.builder()
        .id(TestDataFactory.randomId())
        .username(TestDataFactory.randomUsername())
        .email(TestDataFactory.randomEmail())
        .build();

// 批量生成
List<UserDTO> users = TestDataFactory.randomUserDTOs(10);
```

### 6.5 避免测试间的依赖

```java
@BeforeEach
void setUp() {
    // 每个测试前清理数据
    userRepository.deleteAll();
    
    // 重置 Mock
    Mockito.reset(userRepository);
    
    // 重置 WireMock
    wireMockServer.resetAll();
}
```

---

## 附录：常用命令

```bash
# 运行所有测试
mvn test

# 运行指定服务的测试
mvn test -pl manage-service

# 运行单元测试（排除集成测试）
mvn test -Dtest=*Test -DfailIfNoTests=false

# 运行集成测试
mvn verify -Dtest=*IntegrationTest

# 生成覆盖率报告
mvn verify jacoco:report

# 只运行特定测试类
mvn test -Dtest=UserServiceTest

# 只运行特定测试方法
mvn test -Dtest=UserServiceTest#createUser_WithValidData_ShouldSucceed
```

---

## 附录：测试注解速查

| 注解 | 说明 | 适用场景 |
|------|------|---------|
| `@Test` | 标记测试方法 | 所有测试 |
| `@DisplayName` | 测试显示名称 | 提高可读性 |
| `@Nested` | 嵌套测试类 | 组织测试 |
| `@BeforeEach` | 每个测试前执行 | 初始化 |
| `@ParameterizedTest` | 参数化测试 | 多参数测试 |
| `@Mock` | 创建 Mock 对象 | 单元测试 |
| `@InjectMocks` | 注入 Mock | 单元测试 |
| `@MockBean` | Spring Mock Bean | Controller 测试 |
| `@WebMvcTest` | Web 层测试 | Controller 测试 |
| `@DataJpaTest` | JPA 测试 | Repository 测试 |
| `@SpringBootTest` | 集成测试 | 完整流程测试 |
| `@ActiveProfiles` | 激活配置 | 环境隔离 |
| `@Transactional` | 事务管理 | 数据回滚 |

