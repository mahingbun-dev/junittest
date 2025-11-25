# Java 单元测试新手入门指南

这份指南专门为 Java 新手和不熟悉测试框架的开发者编写，用最简单的语言解释单元测试的核心概念。

## 目录

1. [什么是单元测试？](#1-什么是单元测试)
2. [为什么要写单元测试？](#2-为什么要写单元测试)
3. [测试框架基础](#3-测试框架基础)
4. [JUnit 5 核心概念](#4-junit-5-核心概念)
5. [Mockito 入门](#5-mockito-入门)
6. [AssertJ 断言库](#6-assertj-断言库)
7. [第一个测试用例](#7-第一个测试用例)
8. [常见问题](#8-常见问题)

---

## 1. 什么是单元测试？

### 1.1 简单理解

**单元测试** = 用代码来测试代码

想象你是一个工厂质检员：
- 产品（你的代码）生产完成后
- 你需要检查它是否正常工作
- 单元测试就是自动化的"质检程序"

### 1.2 举个例子

假设你写了一个计算器程序：

```java
// 这是你写的代码
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

单元测试就是验证这个 `add` 方法是否正确：

```java
// 这是测试代码
@Test
void testAdd() {
    Calculator calc = new Calculator();
    int result = calc.add(2, 3);
    
    // 验证：2 + 3 应该等于 5
    assertEquals(5, result);
}
```

### 1.3 "单元"是什么意思？

- **单元** = 代码的最小可测试部分
- 通常是一个方法或一个类
- 测试时只关注这一个单元，不关心其他部分

---

## 2. 为什么要写单元测试？

### 2.1 测试的好处

| 好处 | 说明 |
|------|------|
| 🐛 发现 Bug | 在代码上线前发现问题 |
| 🔄 防止回归 | 修改代码后，测试能发现是否破坏了原有功能 |
| 📖 文档作用 | 测试代码展示了方法应该如何使用 |
| 💪 重构信心 | 有测试保护，可以大胆重构代码 |
| ⏰ 节省时间 | 自动测试比手动测试快得多 |

### 2.2 没有测试会怎样？

```
开发流程（没有测试）：
写代码 → 手动测试 → 发现 Bug → 修复 → 手动测试 → 上线 → 用户发现 Bug → 紧急修复 → ...

开发流程（有测试）：
写代码 → 写测试 → 运行测试 → 发现问题 → 修复 → 运行测试 → 上线 → 稳定运行
```

---

## 3. 测试框架基础

### 3.1 本项目使用的框架

| 框架 | 作用 | 类比 |
|------|------|------|
| **JUnit 5** | 运行测试 | 考试系统 |
| **Mockito** | 模拟依赖 | 替身演员 |
| **AssertJ** | 验证结果 | 标准答案 |
| **Spring Boot Test** | Spring 测试支持 | 考场环境 |

### 3.2 框架之间的关系

```
┌─────────────────────────────────────────────────────────┐
│                    Spring Boot Test                     │
│  (提供 Spring 测试支持，如 @SpringBootTest, MockMvc)    │
├─────────────────────────────────────────────────────────┤
│                                                         │
│   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│   │   JUnit 5   │  │   Mockito   │  │   AssertJ   │   │
│   │             │  │             │  │             │   │
│   │  @Test      │  │   @Mock     │  │ assertThat  │   │
│   │  @BeforeEach│  │   given()   │  │ isEqualTo() │   │
│   │  @Nested    │  │   then()    │  │ hasSize()   │   │
│   └─────────────┘  └─────────────┘  └─────────────┘   │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 4. JUnit 5 核心概念

### 4.1 常用注解速查

```java
// 标记测试方法
@Test
void myTest() { }

// 显示友好的测试名称
@DisplayName("用户登录测试")
@Test
void testLogin() { }

// 每个测试方法执行前运行
@BeforeEach
void setUp() { }

// 每个测试方法执行后运行
@AfterEach
void tearDown() { }

// 所有测试方法执行前运行一次（必须是 static）
@BeforeAll
static void init() { }

// 组织相关测试
@Nested
class LoginTests { }
```

### 4.2 图解测试执行流程

```
┌─────────────────────────────────────────────────────┐
│                   测试类执行流程                      │
├─────────────────────────────────────────────────────┤
│                                                     │
│   @BeforeAll ──────────────────────────────────┐   │
│        │                                        │   │
│        ▼                                        │   │
│   ┌─────────────────────────────────────────┐  │   │
│   │  @BeforeEach                             │  │   │
│   │       │                                  │  │   │
│   │       ▼                                  │  │   │
│   │  @Test void test1() { ... }             │  │   │
│   │       │                                  │  │   │
│   │       ▼                                  │  │   │
│   │  @AfterEach                              │  │   │
│   └─────────────────────────────────────────┘  │   │
│                      │                          │   │
│                      ▼                          │   │
│   ┌─────────────────────────────────────────┐  │   │
│   │  @BeforeEach                             │  │   │
│   │       │                                  │  │   │
│   │       ▼                                  │  │   │
│   │  @Test void test2() { ... }             │  │   │
│   │       │                                  │  │   │
│   │       ▼                                  │  │   │
│   │  @AfterEach                              │  │   │
│   └─────────────────────────────────────────┘  │   │
│                      │                          │   │
│                      ▼                          │   │
│   @AfterAll ◄───────────────────────────────────┘   │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### 4.3 测试命名规范

推荐格式：`方法名_场景_预期结果`

```java
// ✅ 好的命名
void createUser_WithValidData_ShouldReturnCreatedUser()
void getUserById_WhenUserNotExists_ShouldThrowException()
void login_WithWrongPassword_ShouldReturnFalse()

// ❌ 不好的命名
void test1()
void testCreateUser()
void shouldWork()
```

---

## 5. Mockito 入门

### 5.1 为什么需要 Mock？

**问题**：测试 UserService 时，它依赖 UserRepository（需要数据库）

```java
public class UserService {
    private UserRepository userRepository;  // 依赖数据库
    
    public User getUserById(Long id) {
        return userRepository.findById(id);  // 需要查数据库
    }
}
```

**困境**：
- 测试需要数据库 → 慢、不稳定
- 数据库数据不确定 → 测试结果不确定
- 需要清理数据 → 麻烦

**解决方案**：用"假的" Repository 替代真的

```java
// 创建一个"假的" UserRepository
UserRepository fakeRepo = mock(UserRepository.class);

// 告诉它：当调用 findById(1) 时，返回我指定的用户
when(fakeRepo.findById(1L)).thenReturn(testUser);

// 现在测试不需要真数据库了！
```

### 5.2 Mock 核心概念

```
┌───────────────────────────────────────────────────────────┐
│                      Mock 的工作原理                        │
├───────────────────────────────────────────────────────────┤
│                                                           │
│   真实情况：                                                │
│   UserService ──调用──> UserRepository ──查询──> Database  │
│                                          ↑                │
│                                        很慢！              │
│                                                           │
│   使用 Mock：                                              │
│   UserService ──调用──> Mock Repository (假的)             │
│                              │                            │
│                              └──> 直接返回你设定的数据      │
│                                   不访问数据库！           │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

### 5.3 Mockito 常用语法

```java
// 1. 创建 Mock 对象
@Mock
private UserRepository userRepository;

// 2. 注入 Mock 到被测类
@InjectMocks
private UserServiceImpl userService;

// 3. 设置 Mock 行为（BDD 风格，推荐）
given(userRepository.findById(1L))
    .willReturn(Optional.of(user));

// 4. 验证 Mock 被调用
then(userRepository).should().findById(1L);

// 5. 验证从未调用
then(userRepository).should(never()).delete(any());
```

### 5.4 常用参数匹配器

```java
// any() - 匹配任何值
given(repo.save(any(User.class))).willReturn(user);

// anyLong() - 匹配任何 Long
given(repo.findById(anyLong())).willReturn(Optional.of(user));

// anyString() - 匹配任何字符串
given(repo.findByUsername(anyString())).willReturn(Optional.of(user));

// eq() - 精确匹配
given(repo.findById(eq(1L))).willReturn(Optional.of(user));
```

---

## 6. AssertJ 断言库

### 6.1 为什么用 AssertJ？

对比 JUnit 原生断言：

```java
// JUnit 原生（可读性差）
assertEquals(expected, actual);
assertTrue(result);
assertNotNull(user);

// AssertJ（更易读）
assertThat(actual).isEqualTo(expected);
assertThat(result).isTrue();
assertThat(user).isNotNull();
```

### 6.2 常用断言

```java
// 基本断言
assertThat(result).isNotNull();            // 不为空
assertThat(result).isNull();               // 为空
assertThat(result).isEqualTo(expected);    // 相等
assertThat(result).isNotEqualTo(other);    // 不相等

// 布尔断言
assertThat(flag).isTrue();
assertThat(flag).isFalse();

// 数字断言
assertThat(count).isPositive();            // 正数
assertThat(count).isZero();                // 零
assertThat(count).isGreaterThan(5);        // 大于5
assertThat(count).isBetween(1, 10);        // 在1-10之间

// 字符串断言
assertThat(str).isEmpty();                 // 空字符串
assertThat(str).isNotBlank();              // 不是空白
assertThat(str).startsWith("hello");       // 以 hello 开头
assertThat(str).contains("world");         // 包含 world

// 集合断言
assertThat(list).isEmpty();                // 空集合
assertThat(list).hasSize(3);               // 大小为3
assertThat(list).contains(item1, item2);   // 包含指定元素
assertThat(list).containsExactly(a, b, c); // 按顺序完全匹配

// 异常断言
assertThatThrownBy(() -> service.doSomething())
    .isInstanceOf(RuntimeException.class)
    .hasMessageContaining("error");
```

---

## 7. 第一个测试用例

### 7.1 完整示例

下面是一个完整的、带详细注释的测试类：

```java
package com.example.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * 【完整测试示例】
 * 
 * 这个测试类演示了如何测试 UserService
 */
@ExtendWith(MockitoExtension.class)  // 启用 Mockito
@DisplayName("用户服务测试")
class UserServiceTest {

    // ====== 1. 声明依赖 ======
    
    @Mock  // 创建假的 Repository
    private UserRepository userRepository;

    @InjectMocks  // 创建真的 Service，注入假的 Repository
    private UserServiceImpl userService;

    // 测试数据
    private User testUser;

    // ====== 2. 初始化 ======
    
    @BeforeEach  // 每个测试前执行
    void setUp() {
        // 准备测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("zhangsan");
        testUser.setEmail("zhangsan@example.com");
    }

    // ====== 3. 测试方法 ======
    
    @Nested  // 分组：查询相关测试
    @DisplayName("查询用户")
    class GetUserTests {

        @Test
        @DisplayName("根据ID查询 - 用户存在")
        void getUserById_WhenUserExists_ShouldReturnUser() {
            // Given - 准备
            // 当调用 findById(1L) 时，返回 testUser
            given(userRepository.findById(1L))
                .willReturn(Optional.of(testUser));

            // When - 执行
            User result = userService.getUserById(1L);

            // Then - 验证
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("zhangsan");
            
            // 验证 Repository 被调用
            then(userRepository).should().findById(1L);
        }

        @Test
        @DisplayName("根据ID查询 - 用户不存在")
        void getUserById_WhenUserNotExists_ShouldThrowException() {
            // Given - 返回空
            given(userRepository.findById(anyLong()))
                .willReturn(Optional.empty());

            // When & Then - 期望抛出异常
            assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("创建用户")
    class CreateUserTests {

        @Test
        @DisplayName("创建成功")
        void createUser_WithValidData_ShouldReturnCreatedUser() {
            // Given
            User inputUser = new User();
            inputUser.setUsername("newuser");
            inputUser.setEmail("new@example.com");
            
            User savedUser = new User();
            savedUser.setId(100L);  // 模拟数据库生成的ID
            savedUser.setUsername("newuser");
            savedUser.setEmail("new@example.com");
            
            given(userRepository.existsByUsername("newuser"))
                .willReturn(false);  // 用户名不存在
            given(userRepository.save(any(User.class)))
                .willReturn(savedUser);  // 保存返回带ID的用户

            // When
            User result = userService.createUser(inputUser);

            // Then
            assertThat(result.getId()).isEqualTo(100L);
            then(userRepository).should().save(any(User.class));
        }
    }
}
```

### 7.2 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 运行指定测试方法
mvn test -Dtest=UserServiceTest#getUserById_WhenUserExists_ShouldReturnUser
```

---

## 8. 常见问题

### Q1: @Mock 和 @MockBean 有什么区别？

| 注解 | 来源 | 使用场景 |
|------|------|---------|
| `@Mock` | Mockito | 纯单元测试，不启动 Spring |
| `@MockBean` | Spring Boot | 需要 Spring 容器的测试 |

```java
// 单元测试用 @Mock
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repo;
}

// Controller 测试用 @MockBean
@WebMvcTest(UserController.class)
class ControllerTest {
    @MockBean
    private UserService service;
}
```

### Q2: given().willReturn() 和 when().thenReturn() 有什么区别？

**没有本质区别**，只是风格不同：

```java
// Mockito 传统风格
when(mock.method()).thenReturn(value);

// BDD 风格（推荐，更易读）
given(mock.method()).willReturn(value);
```

### Q3: 为什么测试方法是 void 的？

测试方法的结果通过**断言**来验证，而不是返回值：

```java
@Test
void testSomething() {
    // 不需要 return
    // 通过断言判断成功/失败
    assertThat(result).isEqualTo(expected);
}
```

### Q4: @Test 方法为什么没有 public？

JUnit 5 中，`@Test` 方法可以是包级别访问（没有修饰符）：

```java
// JUnit 4（需要 public）
@Test
public void test() { }

// JUnit 5（不需要 public）
@Test
void test() { }
```

### Q5: 如何调试失败的测试？

1. **查看错误信息**：仔细阅读 AssertJ 的错误描述
2. **添加日志**：使用 `System.out.println()` 打印中间值
3. **使用 IDE 调试**：在测试方法上设置断点，Debug 模式运行

---

## 附录：快速参考卡

### JUnit 5 注解

```
@Test              - 标记测试方法
@DisplayName       - 设置显示名称
@BeforeEach        - 每个测试前执行
@AfterEach         - 每个测试后执行
@BeforeAll         - 所有测试前执行（static）
@AfterAll          - 所有测试后执行（static）
@Nested            - 嵌套测试类
@Disabled          - 禁用测试
@ParameterizedTest - 参数化测试
```

### Mockito 语法

```
@Mock              - 创建 Mock 对象
@InjectMocks       - 注入 Mock 到被测类
given().willReturn() - 设置返回值
given().willThrow()  - 设置抛出异常
then().should()    - 验证调用
then().should(never()) - 验证未调用
any(), anyLong(), anyString() - 参数匹配器
```

### AssertJ 断言

```
assertThat(x).isNotNull()
assertThat(x).isEqualTo(y)
assertThat(x).isTrue() / isFalse()
assertThat(list).hasSize(n)
assertThat(list).contains(item)
assertThatThrownBy(() -> ...).isInstanceOf(...)
```

