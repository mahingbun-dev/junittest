package com.example.util;

import com.example.dto.UserDTO;
import com.example.entity.User;
import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 测试数据工厂
 * 
 * 用于生成测试数据，支持：
 * - 固定测试数据
 * - 随机测试数据（使用 Faker）
 * - 批量测试数据生成
 */
public class TestDataFactory {

    private static final Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);

    // ==================== User 相关测试数据 ====================

    /**
     * 创建默认的测试用户实体
     */
    public static User createDefaultUser() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .fullName("测试用户")
                .status(User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 创建随机用户实体
     */
    public static User createRandomUser() {
        String username = faker.name().username();
        return User.builder()
                .username(username)
                .email(username + "@example.com")
                .password("password" + faker.number().digits(3))
                .fullName(faker.name().fullName())
                .status(User.UserStatus.ACTIVE)
                .build();
    }

    /**
     * 创建指定状态的用户实体
     */
    public static User createUserWithStatus(User.UserStatus status) {
        User user = createRandomUser();
        user.setStatus(status);
        return user;
    }

    /**
     * 创建用户实体（不含ID，用于新增）
     */
    public static User createUserWithoutId() {
        return User.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .fullName("新用户")
                .status(User.UserStatus.ACTIVE)
                .build();
    }

    /**
     * 批量创建随机用户实体
     */
    public static List<User> createRandomUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createRandomUser());
        }
        return users;
    }

    // ==================== UserDTO 相关测试数据 ====================

    /**
     * 创建默认的测试用户DTO
     */
    public static UserDTO createDefaultUserDTO() {
        return UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .fullName("测试用户")
                .status("ACTIVE")
                .build();
    }

    /**
     * 创建用于新增的用户DTO（不含ID）
     */
    public static UserDTO createUserDTOForCreate() {
        return UserDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .fullName("新用户")
                .build();
    }

    /**
     * 创建随机用户DTO
     */
    public static UserDTO createRandomUserDTO() {
        String username = faker.name().username();
        return UserDTO.builder()
                .username(username)
                .email(username + "@example.com")
                .password("password" + faker.number().digits(3))
                .fullName(faker.name().fullName())
                .status("ACTIVE")
                .build();
    }

    /**
     * 创建用于更新的用户DTO
     */
    public static UserDTO createUserDTOForUpdate() {
        return UserDTO.builder()
                .username("updateduser")
                .email("updated@example.com")
                .fullName("更新后的用户")
                .status("ACTIVE")
                .build();
    }

    /**
     * 创建无效的用户DTO（用于验证测试）
     */
    public static UserDTO createInvalidUserDTO() {
        return UserDTO.builder()
                .username("") // 空用户名
                .email("invalid-email") // 无效邮箱
                .password("123") // 密码太短
                .build();
    }

    /**
     * 批量创建随机用户DTO
     */
    public static List<UserDTO> createRandomUserDTOs(int count) {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userDTOs.add(createRandomUserDTO());
        }
        return userDTOs;
    }

    // ==================== 工具方法 ====================

    /**
     * 获取 Faker 实例（供需要更多自定义数据的场景使用）
     */
    public static Faker getFaker() {
        return faker;
    }
}

