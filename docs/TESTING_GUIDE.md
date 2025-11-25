# Spring Boot 单元测试框架使用指南

本文档详细说明如何在已有的 Spring Boot 项目中集成和使用此单元测试框架。

## 目录

1. [测试框架概述](#1-测试框架概述)
2. [快速开始](#2-快速开始)
3. [测试颗粒度说明](#3-测试颗粒度说明)
4. [各层测试详解](#4-各层测试详解)
5. [测试最佳实践](#5-测试最佳实践)
6. [常用命令](#6-常用命令)
7. [常见问题](#7-常见问题)

---

## 1. 测试框架概述

### 1.1 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.x | 基础框架 |
| JUnit 5 | 5.8.x | 测试框架 |
| Mockito | 4.5.x | Mock 框架 |
| AssertJ | 3.22.x | 流式断言库 |
| H2 Database | - | 内存数据库 |
| JaCoCo | 0.8.x | 代码覆盖率 |
| ArchUnit | 1.0.x | 架构测试 |

### 1.2 项目测试目录结构

```
src/test/java/com/example/
├── base/                          # 测试基类
│   ├── BaseUnitTest.java          # 单元测试基类
│   ├── BaseIntegrationTest.java   # 集成测试基类
│   ├── BaseWebMvcTest.java        # Web MVC 测试基类
│   └── BaseRepositoryTest.java    # Repository 测试基类
├── config/                        # 测试配置
│   └── TestConfig.java
├── util/                          # 测试工具类
│   ├── TestDataFactory.java       # 测试数据工厂
│   └── TestAssertions.java        # 自定义断言
├── unit/                          # 单元测试
│   ├── service/
│   ├── entity/
│   └── dto/
├── repository/                    # Repository 测试
├── controller/                    # Controller 测试
├── integration/                   # 集成测试
└── architecture/                  # 架构测试
```

---

## 2. 快速开始

### 2.1 添加依赖到现有项目

在你的 `pom.xml` 中添加以下依赖：

```xml
<!-- Spring Boot 测试启动器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Mockito Inline (用于 Mock final 类和静态方法) -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>4.5.1</version>
    <scope>test</scope>
</dependency>

<!-- Faker (测试数据生成) -->
<dependency>
    <groupId>com.github.javafaker</groupId>
    <artifactId>javafaker</artifactId>
    <version>1.0.2</version>
    <scope>test</scope>
</dependency>

<!-- ArchUnit (架构测试) -->
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.0.1</version>
    <scope>test</scope>
</dependency>
```

### 2.2 创建测试配置文件

在 `src/test/resources/` 下创建 `application-test.yml`：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false

logging:
  level:
    root: WARN
    com.example: INFO
```

### 2.3 复制测试基类

将 `src/test/java/com/example/base/` 目录下的基类复制到你的项目中，并修改包名。

---

## 3. 测试颗粒度说明

### 3.1 测试金字塔

```
                    /\
                   /  \
                  / E2E \           <- 端到端测试（最少）
                 /------\
                /  集成  \          <- 集成测试
               /----------\
              /  Controller \       <- 控制器测试
             /--------------\
            /   Repository   \      <- 数据访问层测试
           /------------------\
          /      Service       \    <- 服务层单元测试
         /----------------------\
        /     Entity / DTO       \  <- 实体/DTO单元测试（最多）
       /--------------------------\
```

### 3.2 各层测试对比

| 测试类型 | 启动 Spring | 数据库 | 执行速度 | 适用场景 |
|---------|------------|--------|---------|---------|
| 单元测试 | ❌ | ❌ | 最快 | 业务逻辑、工具类 |
| Repository 测试 | ⚡部分 | ✅ H2 | 快 | JPA 查询方法 |
| Controller 测试 | ⚡部分 | ❌ | 快 | HTTP 接口 |
| 集成测试 | ✅ | ✅ H2 | 较慢 | 端到端流程 |
| 架构测试 | ❌ | ❌ | 快 | 代码规范检查 |

---

## 4. 各层测试详解

### 4.1 Service 层单元测试

**适用场景**：测试业务逻辑，不启动 Spring 容器

**示例代码**：

```java
package com.example.unit.service;

import com.example.base.BaseUnitTest;
import com.example.repository.UserRepository;
import com.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("UserService 单元测试")
class UserServiceUnitTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("创建用户成功")
    void createUser_WithValidData_ShouldReturnCreatedUser() {
        // Given - 准备测试数据和 Mock 行为
        UserDTO inputDTO = TestDataFactory.createUserDTOForCreate();
        given(userRepository.existsByUsername(inputDTO.getUsername())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // When - 执行被测方法
        UserDTO result = userService.createUser(inputDTO);

        // Then - 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(inputDTO.getUsername());
        
        // 验证 Mock 调用
        then(userRepository).should().save(any(User.class));
    }
}
```

**关键注解说明**：
- `@ExtendWith(MockitoExtension.class)` - 启用 Mockito 扩展
- `@Mock` - 创建 Mock 对象
- `@InjectMocks` - 自动注入 Mock 到被测对象

### 4.2 Repository 层测试

**适用场景**：测试 JPA Repository 方法，使用真实数据库

**示例代码**：

```java
package com.example.repository;

import com.example.base.BaseRepositoryTest;
import com.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserRepository 测试")
class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        User user = TestDataFactory.createUserWithoutId();
        savedUser = entityManager.persistAndFlush(user);
    }

    @Test
    @DisplayName("根据用户名查找用户")
    void findByUsername_ShouldReturnUser() {
        // When
        Optional<User> found = userRepository.findByUsername(savedUser.getUsername());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("自定义 JPQL 查询")
    void countByStatus_ShouldReturnCorrectCount() {
        // When
        long count = userRepository.countByStatus(User.UserStatus.ACTIVE);

        // Then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}
```

**关键注解说明**：
- `@DataJpaTest` - 只加载 JPA 相关组件
- `@Autowired TestEntityManager` - 用于直接操作数据库

### 4.3 Controller 层测试

**适用场景**：测试 HTTP 接口，模拟 HTTP 请求

**示例代码**：

```java
package com.example.controller;

import com.example.base.BaseWebMvcTest;
import com.example.dto.UserDTO;
import com.example.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("UserController 测试")
class UserControllerTest extends BaseWebMvcTest {

    @MockBean
    private UserService userService;

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(savedDTO.getUsername()));
    }

    @Test
    @DisplayName("获取用户 - 返回 200")
    void getUserById_WithExistingId_ShouldReturn200() throws Exception {
        // Given
        UserDTO userDTO = TestDataFactory.createDefaultUserDTO();
        given(userService.getUserById(1L)).willReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
```

**关键注解说明**：
- `@WebMvcTest(XxxController.class)` - 只加载指定 Controller
- `@MockBean` - 在 Spring 上下文中创建 Mock Bean
- `MockMvc` - 模拟 HTTP 请求

### 4.4 集成测试

**适用场景**：测试完整业务流程，启动完整 Spring 容器

**示例代码**：

```java
package com.example.integration;

import com.example.base.BaseIntegrationTest;
import com.example.dto.UserDTO;
import com.example.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("用户模块集成测试")
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("完整的用户 CRUD 流程")
    void fullUserCrudFlow() throws Exception {
        // 1. 创建用户
        UserDTO createDTO = UserDTO.builder()
                .username("integrationuser")
                .email("integration@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());

        // 2. 验证数据库
        assertThat(userRepository.existsByUsername("integrationuser")).isTrue();
    }
}
```

**关键注解说明**：
- `@SpringBootTest` - 启动完整 Spring 容器
- `@Transactional` - 测试后自动回滚
- `@ActiveProfiles("test")` - 使用测试配置

### 4.5 参数化测试

**适用场景**：用不同参数多次执行同一测试逻辑

**示例代码**：

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class ParameterizedTests {

    // 使用值列表
    @ParameterizedTest(name = "测试ID: {0}")
    @ValueSource(longs = {1L, 2L, 100L})
    void testWithValueSource(Long id) {
        assertThat(id).isPositive();
    }

    // 使用枚举
    @ParameterizedTest(name = "状态: {0}")
    @EnumSource(User.UserStatus.class)
    void testWithEnumSource(User.UserStatus status) {
        assertThat(status).isNotNull();
    }

    // 使用 CSV 数据
    @ParameterizedTest
    @CsvSource({
        "user1, user1@example.com",
        "user2, user2@example.com"
    })
    void testWithCsvSource(String username, String email) {
        assertThat(username).isNotBlank();
        assertThat(email).contains("@");
    }

    // 使用方法提供数据
    @ParameterizedTest
    @MethodSource("provideTestData")
    void testWithMethodSource(UserDTO dto) {
        assertThat(dto).isNotNull();
    }

    static Stream<UserDTO> provideTestData() {
        return Stream.of(
            TestDataFactory.createRandomUserDTO(),
            TestDataFactory.createRandomUserDTO()
        );
    }
}
```

### 4.6 架构测试

**适用场景**：验证代码架构是否符合设计规范

**示例代码**：

```java
package com.example.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages("com.example");

    @Test
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller");

        rule.check(classes);
    }

    @Test
    void controllersShouldNotAccessRepositories() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..repository..");

        rule.check(classes);
    }
}
```

---

## 5. 测试最佳实践

### 5.1 命名规范

```
方法名_测试场景_预期结果

示例：
- createUser_WithValidData_ShouldReturnCreatedUser
- getUserById_WithNonExistingId_ShouldThrowException
- deleteUser_WhenUserNotFound_ShouldReturn404
```

### 5.2 测试结构 (AAA 模式)

```java
@Test
void testMethod() {
    // Arrange (Given) - 准备测试数据
    User user = TestDataFactory.createDefaultUser();
    given(userRepository.findById(1L)).willReturn(Optional.of(user));

    // Act (When) - 执行被测方法
    UserDTO result = userService.getUserById(1L);

    // Assert (Then) - 验证结果
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(user.getUsername());
}
```

### 5.3 使用 @Nested 组织测试

```java
@DisplayName("UserService 测试")
class UserServiceTest {

    @Nested
    @DisplayName("创建用户测试")
    class CreateUserTests {
        @Test void createUser_WithValidData_ShouldSucceed() { }
        @Test void createUser_WithDuplicateUsername_ShouldFail() { }
    }

    @Nested
    @DisplayName("查询用户测试")
    class GetUserTests {
        @Test void getUserById_WithExistingId_ShouldReturnUser() { }
        @Test void getUserById_WithNonExistingId_ShouldThrowException() { }
    }
}
```

### 5.4 使用 AssertJ 流式断言

```java
// 基本断言
assertThat(result).isNotNull();
assertThat(result.getName()).isEqualTo("expected");

// 集合断言
assertThat(users)
    .hasSize(3)
    .extracting(User::getUsername)
    .containsExactly("user1", "user2", "user3");

// 异常断言
assertThatThrownBy(() -> userService.getUserById(999L))
    .isInstanceOf(ResourceNotFoundException.class)
    .hasMessageContaining("用户");

// 软断言（不会在第一个失败时停止）
SoftAssertions.assertSoftly(softly -> {
    softly.assertThat(user.getName()).isEqualTo("expected");
    softly.assertThat(user.getEmail()).contains("@");
});
```

### 5.5 使用 BDD Mockito 风格

```java
// Given - 设置 Mock 行为
given(userRepository.findById(1L)).willReturn(Optional.of(user));

// When - 执行被测方法
UserDTO result = userService.getUserById(1L);

// Then - 验证 Mock 调用
then(userRepository).should().findById(1L);
then(userRepository).should(never()).save(any());
```

---

## 6. 常用命令

### 6.1 运行测试

```bash
# 运行所有测试
mvn test

# 运行单元测试（排除集成测试）
mvn test -P unit-tests

# 运行集成测试
mvn verify -P integration-tests

# 运行所有测试（包括集成测试）
mvn verify -P all-tests

# 运行指定测试类
mvn test -Dtest=UserServiceUnitTest

# 运行指定测试方法
mvn test -Dtest=UserServiceUnitTest#createUser_WithValidData_ShouldReturnCreatedUser

# 运行匹配模式的测试
mvn test -Dtest=*ServiceTest
```

### 6.2 代码覆盖率

```bash
# 生成覆盖率报告
mvn verify

# 报告位置
# 单元测试: target/site/jacoco-ut/index.html
# 集成测试: target/site/jacoco-it/index.html
# 合并报告: target/site/jacoco-merged/index.html
```

### 6.3 跳过测试

```bash
# 跳过所有测试
mvn package -DskipTests

# 跳过测试编译
mvn package -Dmaven.test.skip=true
```

---

## 7. 常见问题

### Q1: 如何 Mock 静态方法？

```java
// 使用 mockito-inline
try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
    LocalDateTime fixedTime = LocalDateTime.of(2024, 1, 1, 0, 0);
    mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);
    
    // 测试代码
}
```

### Q2: 如何测试异步方法？

```java
// 使用 Awaitility
@Test
void asyncMethod_ShouldCompleteInTime() {
    // 触发异步操作
    service.asyncOperation();
    
    // 等待结果
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(() -> service.isCompleted());
}
```

### Q3: 测试数据库事务回滚不生效？

确保：
1. 测试类添加 `@Transactional` 注解
2. 使用 Spring 的事务管理器
3. 不要在测试方法中使用新线程

### Q4: MockMvc 返回 404？

检查：
1. `@WebMvcTest` 是否指定了正确的 Controller
2. 请求路径是否正确
3. HTTP 方法是否匹配

### Q5: 集成测试太慢？

优化方案：
1. 使用 `@DirtiesContext(classMode = ClassMode.AFTER_CLASS)` 减少上下文重建
2. 减少集成测试数量，增加单元测试
3. 使用 TestContainers 替代启动真实数据库

---

## 附录：测试注解速查表

| 注解 | 说明 | 使用场景 |
|------|------|---------|
| `@Test` | 标记测试方法 | 所有测试 |
| `@DisplayName` | 测试显示名称 | 提高可读性 |
| `@BeforeEach` | 每个测试前执行 | 初始化 |
| `@AfterEach` | 每个测试后执行 | 清理 |
| `@BeforeAll` | 所有测试前执行（静态） | 一次性初始化 |
| `@Nested` | 嵌套测试类 | 组织测试 |
| `@ParameterizedTest` | 参数化测试 | 多参数测试 |
| `@Mock` | 创建 Mock 对象 | 单元测试 |
| `@InjectMocks` | 注入 Mock | 单元测试 |
| `@MockBean` | Spring Mock Bean | Controller 测试 |
| `@WebMvcTest` | Web 层测试 | Controller 测试 |
| `@DataJpaTest` | JPA 测试 | Repository 测试 |
| `@SpringBootTest` | 集成测试 | 完整流程测试 |
| `@ActiveProfiles` | 激活配置 | 环境隔离 |
| `@Transactional` | 事务管理 | 数据回滚 |

