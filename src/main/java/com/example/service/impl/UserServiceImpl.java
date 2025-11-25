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
 * 【用户服务实现类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是 Service 层？
 * ═══════════════════════════════════════════════════════════════
 * 
 * Service 层是业务逻辑层，负责处理业务规则。
 * 
 * 三层架构：
 * Controller 层 → 接收请求，调用 Service
 *       ↓
 * Service 层   → 处理业务逻辑，调用 Repository
 *       ↓
 * Repository 层 → 与数据库交互
 * 
 * Service 层的职责：
 * - 实现业务逻辑（如：用户名不能重复）
 * - 数据转换（Entity ↔ DTO）
 * - 事务管理
 * - 调用多个 Repository 完成复杂操作
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 注解详解
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【@Slf4j】Lombok 注解
 * - 自动生成日志对象 log
 * - 相当于：private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
 * - 使用：log.info("消息"), log.error("错误"), log.debug("调试")
 * 
 * 【@Service】Spring 注解
 * - 标记这是一个 Service 组件
 * - Spring 会自动创建这个类的实例并管理
 * - 可以被 @Autowired 注入到其他地方使用
 * 
 * 【@RequiredArgsConstructor】Lombok 注解
 * - 自动生成包含所有 final 字段的构造函数
 * - 配合 Spring 的构造函数注入，自动注入依赖
 * - 比 @Autowired 字段注入更推荐（更容易测试）
 * 
 * 效果等同于：
 * <pre>
 * public UserServiceImpl(UserRepository userRepository) {
 *     this.userRepository = userRepository;
 * }
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 事务管理说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * 【@Transactional】Spring 事务注解
 * 
 * 什么是事务？
 * - 事务是一组操作，要么全部成功，要么全部失败
 * - 例如：转账操作 = 扣钱 + 加钱，两个操作必须同时成功
 * 
 * @Transactional 的作用：
 * - 方法开始时自动开启事务
 * - 方法正常结束时自动提交事务
 * - 方法抛出异常时自动回滚事务
 * 
 * 常用参数：
 * - readOnly = true：只读事务，用于查询方法，性能更好
 * - 不写参数：读写事务，用于增删改方法
 */
@Slf4j                      // 自动生成日志对象
@Service                    // 标记为 Service 组件
@RequiredArgsConstructor    // 自动生成构造函数注入
public class UserServiceImpl implements UserService {

    /**
     * 用户数据访问对象
     * 
     * final 关键字表示这个字段在创建后不能被修改
     * 配合 @RequiredArgsConstructor 实现构造函数注入
     */
    private final UserRepository userRepository;

    /**
     * 【创建用户】
     * 
     * 业务逻辑：
     * 1. 检查用户名是否已存在
     * 2. 检查邮箱是否已存在
     * 3. 将 DTO 转换为实体
     * 4. 保存到数据库
     * 5. 将保存后的实体转换为 DTO 返回
     * 
     * @param userDTO 用户数据传输对象
     * @return 创建成功的用户信息
     * @throws BusinessException 如果用户名或邮箱已存在
     * 
     * 【@Override 注解】
     * 表示这个方法是实现接口或覆盖父类的方法
     * 如果签名不匹配会编译报错，避免拼写错误
     */
    @Override
    @Transactional  // 开启事务：如果出错会自动回滚
    public UserDTO createUser(UserDTO userDTO) {
        // 记录日志：{} 是占位符，会被后面的参数替换
        log.info("创建用户: {}", userDTO.getUsername());

        // 【业务规则1】检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            // 抛出业务异常，会被全局异常处理器捕获并返回给前端
            throw new BusinessException("用户名已存在: " + userDTO.getUsername());
        }

        // 【业务规则2】检查邮箱是否已存在
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("邮箱已存在: " + userDTO.getEmail());
        }

        // 【数据转换】DTO → Entity
        // toEntity() 是 UserDTO 类中定义的转换方法
        User user = userDTO.toEntity();
        
        // 【保存数据】调用 Repository 保存到数据库
        // save() 方法会返回保存后的实体（包含自动生成的 ID）
        User savedUser = userRepository.save(user);
        
        log.info("用户创建成功, ID: {}", savedUser.getId());

        // 【数据转换】Entity → DTO 并返回
        return UserDTO.fromEntity(savedUser);
    }

    /**
     * 【根据ID获取用户】
     * 
     * @param id 用户ID
     * @return 用户信息
     * @throws ResourceNotFoundException 如果用户不存在
     * 
     * 【readOnly = true 说明】
     * 这是一个只读操作，不会修改数据
     * 设置 readOnly = true 可以提高性能
     */
    @Override
    @Transactional(readOnly = true)  // 只读事务
    public UserDTO getUserById(Long id) {
        log.info("根据ID获取用户: {}", id);
        
        /*
         * 【Optional 和 orElseThrow 说明】
         * 
         * findById() 返回 Optional<User>，而不是直接返回 User
         * Optional 是 Java 8 引入的，用于处理可能为空的值
         * 
         * 传统写法（容易出现空指针异常）：
         * User user = userRepository.findById(id);
         * if (user == null) {
         *     throw new ResourceNotFoundException(...);
         * }
         * return UserDTO.fromEntity(user);
         * 
         * Optional 写法（更安全、更简洁）：
         * userRepository.findById(id)
         *     .orElseThrow(() -> new ResourceNotFoundException(...));
         * 
         * orElseThrow() 的逻辑：
         * - 如果有值：返回该值
         * - 如果为空：抛出指定的异常
         * 
         * () -> new Exception() 是 Lambda 表达式，表示"创建一个异常"
         */
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        
        return UserDTO.fromEntity(user);
    }

    /**
     * 【根据用户名获取用户】
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        log.info("根据用户名获取用户: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "username", username));
        return UserDTO.fromEntity(user);
    }

    /**
     * 【获取所有用户】
     * 
     * @return 所有用户的列表
     * 
     * 【Stream API 说明】
     * Java 8 引入的流式处理，让集合操作更简洁。
     * 
     * 传统写法：
     * <pre>
     * List<User> users = userRepository.findAll();
     * List<UserDTO> dtos = new ArrayList<>();
     * for (User user : users) {
     *     dtos.add(UserDTO.fromEntity(user));
     * }
     * return dtos;
     * </pre>
     * 
     * Stream 写法：
     * <pre>
     * return userRepository.findAll()    // 获取所有用户
     *         .stream()                  // 转换为流
     *         .map(UserDTO::fromEntity)  // 对每个元素执行转换
     *         .collect(Collectors.toList());  // 收集为 List
     * </pre>
     * 
     * UserDTO::fromEntity 是方法引用，等同于 user -> UserDTO.fromEntity(user)
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("获取所有用户");
        return userRepository.findAll()           // 查询所有用户
                .stream()                         // 转为流
                .map(UserDTO::fromEntity)         // 每个 User 转为 UserDTO
                .collect(Collectors.toList());    // 收集为 List
    }

    /**
     * 【根据状态获取用户列表】
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByStatus(User.UserStatus status) {
        log.info("根据状态获取用户列表: {}", status);
        return userRepository.findByStatus(status).stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 【更新用户】
     * 
     * 业务逻辑：
     * 1. 根据 ID 查找用户，不存在则抛异常
     * 2. 检查新用户名是否与其他用户冲突
     * 3. 检查新邮箱是否与其他用户冲突
     * 4. 只更新非空的字段
     * 5. 保存并返回
     */
    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("更新用户, ID: {}", id);

        // 查找要更新的用户
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));

        // 检查新用户名是否被其他用户使用
        // 条件：新用户名不为空 && 新用户名与当前不同 && 新用户名已存在
        if (userDTO.getUsername() != null && 
            !userDTO.getUsername().equals(existingUser.getUsername()) &&
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BusinessException("用户名已存在: " + userDTO.getUsername());
        }

        // 检查新邮箱是否被其他用户使用
        if (userDTO.getEmail() != null && 
            !userDTO.getEmail().equals(existingUser.getEmail()) &&
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BusinessException("邮箱已存在: " + userDTO.getEmail());
        }

        // 【只更新非空字段】
        // 这种方式允许部分更新：只传需要修改的字段
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
            // 将字符串状态转换为枚举
            existingUser.setStatus(User.UserStatus.valueOf(userDTO.getStatus()));
        }

        // 保存更新后的用户
        User updatedUser = userRepository.save(existingUser);
        log.info("用户更新成功, ID: {}", updatedUser.getId());

        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * 【删除用户】
     * 
     * @param id 要删除的用户ID
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户, ID: {}", id);

        // 先检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }

        // 执行删除
        userRepository.deleteById(id);
        log.info("用户删除成功, ID: {}", id);
    }

    /**
     * 【检查用户名是否存在】
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 【检查邮箱是否存在】
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 【根据状态统计用户数量】
     */
    @Override
    @Transactional(readOnly = true)
    public long countByStatus(User.UserStatus status) {
        return userRepository.countByStatus(status);
    }
}
