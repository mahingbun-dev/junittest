package com.example.service;

import com.example.dto.UserDTO;
import com.example.entity.User;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 创建用户
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * 根据ID获取用户
     */
    UserDTO getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    UserDTO getUserByUsername(String username);

    /**
     * 获取所有用户
     */
    List<UserDTO> getAllUsers();

    /**
     * 根据状态获取用户列表
     */
    List<UserDTO> getUsersByStatus(User.UserStatus status);

    /**
     * 更新用户
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据状态统计用户数量
     */
    long countByStatus(User.UserStatus status);
}

