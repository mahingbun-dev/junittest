package com.example.unit.entity;

import com.example.base.BaseUnitTest;
import com.example.entity.User;
import com.example.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User 实体单元测试
 * 
 * 测试颗粒度：单元测试 (Unit Test)
 * 测试目标：User 实体类的基本功能
 */
@DisplayName("User 实体单元测试")
class UserEntityTest extends BaseUnitTest {

    @Nested
    @DisplayName("Builder 模式测试")
    class BuilderTests {

        @Test
        @DisplayName("使用 Builder 创建 User 实体")
        void builder_ShouldCreateUserWithAllFields() {
            // When
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("password123")
                    .fullName("Test User")
                    .status(User.UserStatus.ACTIVE)
                    .build();

            // Then
            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("password123");
            assertThat(user.getFullName()).isEqualTo("Test User");
            assertThat(user.getStatus()).isEqualTo(User.UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("Builder 默认值测试 - status 默认为 ACTIVE")
        void builder_ShouldSetDefaultStatus() {
            // When
            User user = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .password("password123")
                    .build();

            // Then
            assertThat(user.getStatus()).isEqualTo(User.UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Getter/Setter 测试")
    class GetterSetterTests {

        @Test
        @DisplayName("测试所有 Getter 和 Setter")
        void getterAndSetter_ShouldWorkCorrectly() {
            // Given
            User user = new User();

            // When
            user.setId(1L);
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword("password123");
            user.setFullName("Test User");
            user.setStatus(User.UserStatus.INACTIVE);

            // Then
            assertThat(user.getId()).isEqualTo(1L);
            assertThat(user.getUsername()).isEqualTo("testuser");
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getPassword()).isEqualTo("password123");
            assertThat(user.getFullName()).isEqualTo("Test User");
            assertThat(user.getStatus()).isEqualTo(User.UserStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("equals 和 hashCode 测试")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("相同属性的对象应该相等")
        void equals_WithSameProperties_ShouldBeEqual() {
            // Given
            User user1 = TestDataFactory.createDefaultUser();
            User user2 = TestDataFactory.createDefaultUser();

            // Then
            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("不同属性的对象不应该相等")
        void equals_WithDifferentProperties_ShouldNotBeEqual() {
            // Given
            User user1 = TestDataFactory.createDefaultUser();
            User user2 = TestDataFactory.createDefaultUser();
            user2.setUsername("differentuser");

            // Then
            assertThat(user1).isNotEqualTo(user2);
        }
    }

    @Nested
    @DisplayName("UserStatus 枚举测试")
    class UserStatusTests {

        @Test
        @DisplayName("验证所有状态值")
        void userStatus_ShouldHaveAllExpectedValues() {
            // Then
            assertThat(User.UserStatus.values())
                    .containsExactly(
                            User.UserStatus.ACTIVE,
                            User.UserStatus.INACTIVE,
                            User.UserStatus.SUSPENDED
                    );
        }

        @Test
        @DisplayName("状态名称转换")
        void userStatus_ValueOf_ShouldWorkCorrectly() {
            assertThat(User.UserStatus.valueOf("ACTIVE")).isEqualTo(User.UserStatus.ACTIVE);
            assertThat(User.UserStatus.valueOf("INACTIVE")).isEqualTo(User.UserStatus.INACTIVE);
            assertThat(User.UserStatus.valueOf("SUSPENDED")).isEqualTo(User.UserStatus.SUSPENDED);
        }
    }
}

