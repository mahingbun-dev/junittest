package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 【用户实体类】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 什么是实体类（Entity）？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 实体类是与数据库表对应的 Java 类。
 * - 一个实体类 = 一张数据库表
 * - 一个属性 = 一个数据库字段
 * - 一个对象实例 = 一条数据库记录
 * 
 * 例如这个 User 类对应数据库的 users 表：
 * 
 * | id | username | email           | password | status | created_at |
 * |----|----------|-----------------|----------|--------|------------|
 * | 1  | zhangsan | zs@example.com  | xxx      | ACTIVE | 2024-01-01 |
 * | 2  | lisi     | ls@example.com  | xxx      | ACTIVE | 2024-01-02 |
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 Lombok 注解详解
 * ═══════════════════════════════════════════════════════════════
 * 
 * Lombok 是一个代码简化工具，通过注解自动生成常见代码。
 * 
 * 【@Data】自动生成：
 * - 所有字段的 getter 方法：getId(), getUsername(), ...
 * - 所有字段的 setter 方法：setId(Long id), setUsername(String username), ...
 * - toString() 方法：打印对象时显示所有字段
 * - equals() 和 hashCode() 方法：用于对象比较
 * 
 * 【@Builder】生成建造者模式代码，让创建对象更优雅：
 * <pre>
 * // 传统方式（繁琐）
 * User user = new User();
 * user.setUsername("张三");
 * user.setEmail("zs@example.com");
 * user.setPassword("123456");
 * 
 * // Builder 方式（优雅）
 * User user = User.builder()
 *         .username("张三")
 *         .email("zs@example.com")
 *         .password("123456")
 *         .build();
 * </pre>
 * 
 * 【@NoArgsConstructor】生成无参构造函数：
 * public User() { }
 * 
 * 【@AllArgsConstructor】生成全参构造函数：
 * public User(Long id, String username, String email, ...) { ... }
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 JPA 注解详解
 * ═══════════════════════════════════════════════════════════════
 * 
 * JPA (Java Persistence API) 是 Java 的数据库持久化标准。
 * 
 * 【@Entity】
 * - 标记这是一个实体类，会被 JPA 管理
 * - JPA 会自动处理这个类与数据库的映射
 * 
 * 【@Table(name = "users")】
 * - 指定对应的数据库表名
 * - 如果不指定，默认用类名（首字母小写）
 * 
 * 【@Id】
 * - 标记主键字段
 * - 每个实体必须有一个主键
 * 
 * 【@GeneratedValue(strategy = GenerationType.IDENTITY)】
 * - 主键生成策略
 * - IDENTITY 表示使用数据库的自增功能
 * - 插入数据时不需要指定 ID，数据库会自动生成
 * 
 * 【@Column】
 * - 字段与数据库列的映射
 * - nullable = false：不允许为空
 * - unique = true：值必须唯一
 * - name = "xxx"：指定列名（默认用字段名）
 * 
 * 【@Enumerated(EnumType.STRING)】
 * - 枚举类型的存储方式
 * - STRING：以字符串形式存储（"ACTIVE", "INACTIVE"）
 * - ORDINAL：以数字形式存储（0, 1, 2）- 不推荐
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 验证注解详解
 * ═══════════════════════════════════════════════════════════════
 * 
 * 验证注解用于数据校验，在保存数据前自动检查。
 * 
 * 【@NotBlank】
 * - 不能为空，且不能只有空格
 * - message 参数指定校验失败时的错误信息
 * 
 * 【@Size(min = 2, max = 50)】
 * - 字符串长度限制
 * - min：最小长度
 * - max：最大长度
 * 
 * 【@Email】
 * - 必须是有效的邮箱格式
 * - 会检查是否包含 @ 符号和域名
 */
@Data                    // Lombok: 自动生成 getter/setter/toString/equals/hashCode
@Builder                 // Lombok: 生成 Builder 模式代码
@NoArgsConstructor       // Lombok: 生成无参构造函数
@AllArgsConstructor      // Lombok: 生成全参构造函数
@Entity                  // JPA: 标记为实体类
@Table(name = "users")   // JPA: 指定表名为 "users"
public class User {

    /**
     * 用户ID - 主键
     * 
     * @Id 标记这是主键字段
     * @GeneratedValue 指定主键生成策略为数据库自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     * 
     * 验证规则：
     * - 不能为空
     * - 长度 2-50 个字符
     * - 必须唯一（不能重复）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50之间")
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 邮箱地址
     * 
     * 验证规则：
     * - 不能为空
     * - 必须是有效的邮箱格式
     * - 必须唯一
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 密码
     * 
     * 验证规则：
     * - 不能为空
     * - 最少 6 个字符
     * 
     * 注意：实际项目中密码应该加密存储！
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    @Column(nullable = false)
    private String password;

    /**
     * 用户全名/昵称
     * 
     * name = "full_name" 指定数据库列名
     * 因为 Java 用驼峰命名（fullName），数据库习惯用下划线（full_name）
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * 用户状态
     * 
     * @Enumerated(EnumType.STRING) 表示以字符串形式存储
     * @Builder.Default 指定使用 Builder 时的默认值
     * 
     * 状态值：ACTIVE（活跃）、INACTIVE（未激活）、SUSPENDED（已停用）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default  // Builder 模式的默认值
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 创建时间
     * 
     * 记录用户是什么时候创建的
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 
     * 记录用户信息最后一次修改的时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 【生命周期回调】创建前自动设置时间
     * 
     * @PrePersist 注解表示在实体被持久化（保存到数据库）之前执行
     * 也就是调用 repository.save() 时，如果是新对象，会自动执行这个方法
     * 
     * 作用：自动设置 createdAt 和 updatedAt 为当前时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // 设置创建时间为当前时间
        updatedAt = LocalDateTime.now();  // 设置更新时间为当前时间
    }

    /**
     * 【生命周期回调】更新前自动设置时间
     * 
     * @PreUpdate 注解表示在实体被更新之前执行
     * 也就是修改已存在的对象并调用 save() 时执行
     * 
     * 作用：自动更新 updatedAt 为当前时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 【用户状态枚举】
     * 
     * 枚举（enum）是一种特殊的类，用于定义一组固定的常量。
     * 
     * 使用枚举的好处：
     * - 类型安全：不能设置为枚举以外的值
     * - 代码清晰：ACTIVE 比 1 或 "active" 更易读
     * - IDE 支持：自动提示可用的值
     * 
     * 使用示例：
     * <pre>
     * user.setStatus(UserStatus.ACTIVE);
     * 
     * if (user.getStatus() == UserStatus.SUSPENDED) {
     *     throw new Exception("用户已被停用");
     * }
     * </pre>
     */
    public enum UserStatus {
        ACTIVE,      // 活跃状态：正常可用
        INACTIVE,    // 未激活：注册后未激活
        SUSPENDED    // 已停用：被管理员禁用
    }
}
