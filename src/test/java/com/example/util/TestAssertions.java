package com.example.util;

import com.example.dto.UserDTO;
import com.example.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 自定义测试断言工具类
 * 
 * 封装常用的断言逻辑，提高测试代码的可读性和复用性
 */
public class TestAssertions {

    /**
     * 断言 User 实体包含预期的基本属性
     */
    public static void assertUserBasicProperties(User user, String expectedUsername, String expectedEmail) {
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
        assertThat(user.getEmail()).isEqualTo(expectedEmail);
    }

    /**
     * 断言 User 实体的完整属性
     */
    public static void assertUserFullProperties(User actual, User expected) {
        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getFullName()).isEqualTo(expected.getFullName());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
    }

    /**
     * 断言 UserDTO 包含预期的基本属性
     */
    public static void assertUserDTOBasicProperties(UserDTO userDTO, String expectedUsername, String expectedEmail) {
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getUsername()).isEqualTo(expectedUsername);
        assertThat(userDTO.getEmail()).isEqualTo(expectedEmail);
    }

    /**
     * 断言 UserDTO 是从 User 实体正确转换的
     */
    public static void assertUserDTOFromEntity(UserDTO userDTO, User user) {
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getId()).isEqualTo(user.getId());
        assertThat(userDTO.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDTO.getFullName()).isEqualTo(user.getFullName());
        assertThat(userDTO.getStatus()).isEqualTo(user.getStatus().name());
        // 注意：密码不应该在 DTO 中返回
        assertThat(userDTO.getPassword()).isNull();
    }

    /**
     * 断言新创建的实体具有有效的 ID 和时间戳
     */
    public static void assertNewEntityProperties(User user) {
        assertThat(user.getId()).isNotNull().isPositive();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    /**
     * 断言两个用户列表内容相等（不考虑顺序）
     */
    public static void assertUserListsEqual(java.util.List<User> actual, java.util.List<User> expected) {
        assertThat(actual)
                .hasSize(expected.size())
                .usingElementComparatorIgnoringFields("createdAt", "updatedAt")
                .containsExactlyInAnyOrderElementsOf(expected);
    }
}

