package com.example.service.impl;

import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.exception.BusinessException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.info("创建用户: {}", userDTO.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("用户名已存在: " + userDTO.getUsername());
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("邮箱已存在: " + userDTO.getEmail());
        }

        User user = userDTO.toEntity();
        User savedUser = userRepository.save(user);
        log.info("用户创建成功, ID: {}", savedUser.getId());

        return UserDTO.fromEntity(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.info("根据ID获取用户: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.info("根据用户名获取用户: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "username", username));
        return UserDTO.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("获取所有用户");
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByStatus(User.UserStatus status) {
        log.info("根据状态获取用户列表: {}", status);
        return userRepository.findByStatus(status).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("更新用户, ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));

        // 检查用户名是否被其他用户使用
        if (userDTO.getUsername() != null && 
            !userDTO.getUsername().equals(existingUser.getUsername()) &&
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("用户名已存在: " + userDTO.getUsername());
        }

        // 检查邮箱是否被其他用户使用
        if (userDTO.getEmail() != null && 
            !userDTO.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("邮箱已存在: " + userDTO.getEmail());
        }

        // 更新字段
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null) {
            existingUser.setPassword(userDTO.getPassword());
        }
        if (userDTO.getFullName() != null) {
            existingUser.setFullName(userDTO.getFullName());
        }
        if (userDTO.getStatus() != null) {
            existingUser.setStatus(User.UserStatus.valueOf(userDTO.getStatus()));
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("用户更新成功, ID: {}", updatedUser.getId());

        return UserDTO.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户, ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }

        userRepository.deleteById(id);
        log.info("用户删除成功, ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(User.UserStatus status) {
        return userRepository.countByStatus(status);
    }
}

