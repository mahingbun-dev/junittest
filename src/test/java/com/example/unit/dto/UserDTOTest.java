package com.example.unit.dto;

import com.example.base.BaseUnitTest;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserDTO 单元测试
 * 
 * 测试颗粒度：单元测试 (Unit Test)
 * 测试目标：UserDTO 的转换方法和基本功能
 */
@DisplayName("UserDTO 单元测试")
class UserDTOTest extends BaseUnitTest {

    @Nested
    @DisplayName("fromEntity 转换测试")
    class FromEntityTests {

        @Test
        @DisplayName("从 User 实体转换为 DTO - 完整属性")
        void fromEntity_WithCompleteUser_ShouldConvertCorrectly() {
            // Given
            User user = TestDataFactory.createDefaultUser();

            // When
            UserDTO dto = UserDTO.fromEntity(user);

            // Then
            assertThat(dto.getId()).isEqualTo(user.getId());
            assertThat(dto.getUsername()).isEqualTo(user.getUsername());
            assertThat(dto.getEmail()).isEqualTo(user.getEmail());
            assertThat(dto.getFullName()).isEqualTo(user.getFullName());
            assertThat(dto.getStatus()).isEqualTo(user.getStatus().name());
            // 密码不应该被转换到 DTO
            assertThat(dto.getPassword()).isNull();
        }

        @Test
        @DisplayName("从 User 实体转换为 DTO - 部分属性为空")
        void fromEntity_WithPartialUser_ShouldHandleNullFields() {
            // Given
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("password")
                    .status(User.UserStatus.ACTIVE)
                    .build();

            // When
            UserDTO dto = UserDTO.fromEntity(user);

            // Then
            assertThat(dto.getFullName()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity 转换测试")
    class ToEntityTests {

        @Test
        @DisplayName("从 DTO 转换为 User 实体 - 完整属性")
        void toEntity_WithCompleteDTO_ShouldConvertCorrectly() {
            // Given
            UserDTO dto = TestDataFactory.createDefaultUserDTO();

            // When
            User user = dto.toEntity();

            // Then
            assertThat(user.getId()).isEqualTo(dto.getId());
            assertThat(user.getUsername()).isEqualTo(dto.getUsername());
            assertThat(user.getEmail()).isEqualTo(dto.getEmail());
            assertThat(user.getPassword()).isEqualTo(dto.getPassword());
            assertThat(user.getFullName()).isEqualTo(dto.getFullName());
            assertThat(user.getStatus().name()).isEqualTo(dto.getStatus());
        }

        @Test
        @DisplayName("从 DTO 转换为 User 实体 - 状态为空使用默认值")
        void toEntity_WithNullStatus_ShouldUseDefaultStatus() {
            // Given
            UserDTO dto = UserDTO.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .password("password123")
                    .build();

            // When
            User user = dto.toEntity();

            // Then
            assertThat(user.getStatus()).isEqualTo(User.UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Builder 测试")
    class BuilderTests {

        @Test
        @DisplayName("使用 Builder 创建 DTO")
        void builder_ShouldCreateDTOWithAllFields() {
            // When
            UserDTO dto = UserDTO.builder()
                    .id(1L)
                    .username("testuser")
                    .email("test@example.com")
                    .password("password123")
                    .fullName("Test User")
                    .status("ACTIVE")
                    .build();

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getUsername()).isEqualTo("testuser");
            assertThat(dto.getEmail()).isEqualTo("test@example.com");
            assertThat(dto.getPassword()).isEqualTo("password123");
            assertThat(dto.getFullName()).isEqualTo("Test User");
            assertThat(dto.getStatus()).isEqualTo("ACTIVE");
        }
    }
}

