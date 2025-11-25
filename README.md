# Spring Boot 2 + Java 8 单元测试框架

一个完整的 Spring Boot 单元测试框架示例项目，覆盖所有测试颗粒度。

## 🎯 项目特点

- ✅ 基于 **Spring Boot 2.7.x** + **Java 8**
- ✅ 使用 **JUnit 5** 测试框架
- ✅ 完整的测试颗粒度覆盖
- ✅ 规范的项目结构和测试模板
- ✅ JaCoCo 代码覆盖率报告
- ✅ ArchUnit 架构测试

## 📁 项目结构

```
junittest/
├── src/main/java/com/example/
│   ├── Application.java              # 应用入口
│   ├── controller/                   # 控制器层
│   │   └── UserController.java
│   ├── service/                      # 服务层
│   │   ├── UserService.java
│   │   └── impl/UserServiceImpl.java
│   ├── repository/                   # 数据访问层
│   │   └── UserRepository.java
│   ├── entity/                       # 实体类
│   │   └── User.java
│   ├── dto/                          # 数据传输对象
│   │   ├── UserDTO.java
│   │   └── ApiResponse.java
│   └── exception/                    # 异常处理
│       ├── BusinessException.java
│       ├── ResourceNotFoundException.java
│       └── GlobalExceptionHandler.java
│
├── src/test/java/com/example/
│   ├── base/                         # 🔷 测试基类
│   │   ├── BaseUnitTest.java         # 单元测试基类
│   │   ├── BaseIntegrationTest.java  # 集成测试基类
│   │   ├── BaseWebMvcTest.java       # Controller 测试基类
│   │   └── BaseRepositoryTest.java   # Repository 测试基类
│   │
│   ├── util/                         # 🔷 测试工具
│   │   ├── TestDataFactory.java      # 测试数据工厂
│   │   └── TestAssertions.java       # 自定义断言
│   │
│   ├── unit/                         # 🔷 单元测试
│   │   ├── service/
│   │   │   └── UserServiceUnitTest.java
│   │   ├── entity/
│   │   │   └── UserEntityTest.java
│   │   └── dto/
│   │       └── UserDTOTest.java
│   │
│   ├── repository/                   # 🔷 Repository 测试
│   │   └── UserRepositoryTest.java
│   │
│   ├── controller/                   # 🔷 Controller 测试
│   │   └── UserControllerTest.java
│   │
│   ├── integration/                  # 🔷 集成测试
│   │   └── UserIntegrationTest.java
│   │
│   └── architecture/                 # 🔷 架构测试
│       └── ArchitectureTest.java
│
├── docs/
│   └── TESTING_GUIDE.md              # 📖 详细使用指南
│
└── pom.xml                           # Maven 配置
```

## 🧪 测试颗粒度

| 测试类型 | 说明 | 基类 | 示例 |
|---------|------|------|------|
| **单元测试** | 测试单个类的业务逻辑 | `BaseUnitTest` | `UserServiceUnitTest` |
| **Entity 测试** | 测试实体类功能 | `BaseUnitTest` | `UserEntityTest` |
| **DTO 测试** | 测试 DTO 转换逻辑 | `BaseUnitTest` | `UserDTOTest` |
| **Repository 测试** | 测试数据访问层 | `BaseRepositoryTest` | `UserRepositoryTest` |
| **Controller 测试** | 测试 HTTP 接口 | `BaseWebMvcTest` | `UserControllerTest` |
| **集成测试** | 测试端到端流程 | `BaseIntegrationTest` | `UserIntegrationTest` |
| **架构测试** | 验证代码架构规范 | - | `ArchitectureTest` |

## 🚀 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+

### 2. 克隆项目

```bash
git clone <repository-url>
cd junittest
```

### 3. 运行测试

```bash
# 运行所有单元测试
mvn test

# 运行集成测试
mvn verify -P integration-tests

# 运行所有测试并生成覆盖率报告
mvn verify
```

### 4. 查看覆盖率报告

```bash
# 打开覆盖率报告
open target/site/jacoco-merged/index.html
```

## 📊 测试命令

```bash
# 运行指定测试类
mvn test -Dtest=UserServiceUnitTest

# 运行指定测试方法
mvn test -Dtest=UserServiceUnitTest#createUser_WithValidData_ShouldReturnCreatedUser

# 只运行单元测试（排除集成测试）
mvn test -P unit-tests

# 只运行集成测试
mvn verify -P integration-tests

# 跳过测试
mvn package -DskipTests
```

## 📖 详细文档

请查看 [TESTING_GUIDE.md](docs/TESTING_GUIDE.md) 获取：

- 各层测试详解和示例代码
- 如何在已有项目中集成测试框架
- 测试最佳实践
- 常见问题解答

## 🛠 技术栈

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| JUnit Jupiter | 5.8.2 | 测试框架 |
| Mockito | 4.5.1 | Mock 框架 |
| AssertJ | 3.22.0 | 断言库 |
| H2 Database | - | 内存数据库 |
| JaCoCo | 0.8.8 | 代码覆盖率 |
| ArchUnit | 1.0.1 | 架构测试 |
| JavaFaker | 1.0.2 | 测试数据生成 |

## 📝 测试命名规范

```
方法名_测试场景_预期结果

示例：
- createUser_WithValidData_ShouldReturnCreatedUser
- getUserById_WithNonExistingId_ShouldThrowException
- deleteUser_WhenUserNotFound_ShouldReturn404
```

## 📄 License

MIT License
