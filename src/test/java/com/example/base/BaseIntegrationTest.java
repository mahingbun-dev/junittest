package com.example.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 集成测试基类
 * 
 * 适用场景：
 * - 需要启动完整 Spring 容器
 * - 测试多个组件之间的协作
 * - 需要真实的数据库操作
 * 
 * 特点：
 * - 启动完整的 Spring 应用上下文
 * - 使用真实的依赖（如数据库）
 * - 测试事务会自动回滚
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseIntegrationTest {

    /**
     * 每个测试方法执行前的初始化
     */
    @BeforeEach
    protected void setUp() {
        // 默认实现为空，子类可覆盖
    }
}

