package com.example.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository 测试基类
 * 
 * 适用场景：
 * - 测试 JPA Repository 层
 * - 需要真实的数据库操作
 * - 测试自定义查询方法
 * 
 * 特点：
 * - 只加载 JPA 相关组件
 * - 使用内存数据库 H2
 * - 测试事务自动回滚
 * - 执行速度较快
 */
@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    /**
     * 每个测试方法执行前的初始化
     */
    @BeforeEach
    protected void setUp() {
        // 默认实现为空，子类可覆盖
    }
}

