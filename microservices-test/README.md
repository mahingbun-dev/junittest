# 微服务单元测试框架

针对 Spring Boot 2 + Java 8 微服务架构（manage、API、job、query）设计的完整单元测试框架。

## 🎯 项目特点

- ✅ 基于 **Spring Boot 2.7.x** + **Java 8**
- ✅ 支持 **4 个微服务**：manage、api、job、query
- ✅ 完整的测试颗粒度覆盖
- ✅ 微服务间调用测试（WireMock）
- ✅ 公共测试模块复用
- ✅ 异步任务测试支持

## 📁 项目结构

```
microservices-test/
├── common-test/                     # 📦 公共测试模块
│   └── src/main/java/com/example/test/
│       ├── base/                    # 测试基类
│       │   ├── BaseUnitTest.java
│       │   ├── BaseIntegrationTest.java
│       │   ├── BaseControllerTest.java
│       │   ├── BaseRepositoryTest.java
│       │   ├── BaseMicroserviceTest.java
│       │   └── BaseJobTest.java
│       ├── util/                    # 工具类
│       │   ├── TestDataFactory.java
│       │   └── JsonTestUtil.java
│       └── mock/                    # 微服务 Mock
│           ├── MockServiceHelper.java
│           ├── ManageServiceMock.java
│           ├── ApiServiceMock.java
│           ├── QueryServiceMock.java
│           └── JobServiceMock.java
│
├── manage-service-test/             # 🔷 Manage 服务测试
│   └── src/test/java/com/example/manage/
│       ├── unit/service/
│       ├── controller/
│       └── integration/
│
├── api-service-test/                # 🔷 API 服务测试
│   └── src/test/java/com/example/api/
│       ├── unit/service/
│       ├── controller/
│       └── integration/
│
├── job-service-test/                # 🔷 Job 服务测试
│   └── src/test/java/com/example/job/
│       ├── unit/service/
│       ├── unit/task/
│       └── integration/
│
├── query-service-test/              # 🔷 Query 服务测试
│   └── src/test/java/com/example/query/
│       ├── unit/service/
│       ├── repository/
│       └── integration/
│
└── MICROSERVICES_TESTING_GUIDE.md   # 📖 详细使用指南
```

## 🧪 测试颗粒度

### 按微服务分类

| 微服务 | 主要测试类型 | 基类 |
|--------|-------------|------|
| **manage** | 用户/权限管理测试 | `BaseUnitTest`, `BaseIntegrationTest` |
| **api** | 认证/网关测试 | `BaseControllerTest`, `BaseMicroserviceTest` |
| **job** | 定时任务/异步测试 | `BaseJobTest` |
| **query** | 查询/缓存测试 | `BaseRepositoryTest` |

### 按测试类型分类

| 测试类型 | 基类 | 说明 |
|---------|------|------|
| 单元测试 | `BaseUnitTest` | 纯业务逻辑，Mockito |
| Controller 测试 | `BaseControllerTest` | MockMvc，HTTP 接口 |
| Repository 测试 | `BaseRepositoryTest` | @DataJpaTest，H2 |
| 集成测试 | `BaseIntegrationTest` | 完整流程，事务回滚 |
| 微服务测试 | `BaseMicroserviceTest` | WireMock 模拟服务 |
| 任务测试 | `BaseJobTest` | Awaitility 异步等待 |

## 🚀 快速开始

### 1. 添加 common-test 依赖

```xml
<dependency>
    <groupId>com.example.test</groupId>
    <artifactId>common-test</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

### 2. 继承对应基类

```java
// 单元测试
class UserServiceTest extends BaseUnitTest { }

// Controller 测试
@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest { }

// 集成测试
class UserIntegrationTest extends BaseIntegrationTest { }

// 微服务调用测试
class ApiGatewayTest extends BaseMicroserviceTest { }

// 定时任务测试
class DataSyncJobTest extends BaseJobTest { }
```

### 3. 使用 Mock 工具

```java
// 模拟 Manage 服务
ManageServiceMock.mockGetUser(1L, "testuser", "test@example.com");

// 模拟 API 服务
ApiServiceMock.mockValidateTokenSuccess("token", 1L, "testuser");

// 模拟 Query 服务
QueryServiceMock.mockPageQuery("users", userList, 100, 0, 10);

// 模拟 Job 服务
JobServiceMock.mockTriggerJob("dataSync", "job-123");
```

## 📖 详细文档

请查看 [MICROSERVICES_TESTING_GUIDE.md](MICROSERVICES_TESTING_GUIDE.md) 获取：

- 各微服务测试策略详解
- 完整的测试用例示例
- 微服务间调用测试方法
- 最佳实践指南

## 🛠 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| JUnit 5 | 5.8.2 | 测试框架 |
| Mockito | 4.5.1 | Mock 框架 |
| WireMock | 2.35.0 | HTTP Mock |
| Awaitility | - | 异步测试 |
| TestContainers | 1.17.6 | 容器测试 |
| H2 | - | 内存数据库 |
| RestAssured | - | API 测试 |

## 📝 测试命名规范

```
方法名_测试场景_预期结果

示例：
- createUser_WithValidData_ShouldReturnCreatedUser
- login_WithInvalidCredentials_ShouldReturn401
- executeJob_WhenAlreadyRunning_ShouldSkip
```

## 📊 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定服务测试
mvn test -pl manage-service-test

# 运行集成测试
mvn verify -P integration-tests

# 生成覆盖率报告
mvn verify jacoco:report
```

## 📄 License

MIT License

