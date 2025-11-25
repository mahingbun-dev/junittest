package com.example.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Repository 测试基类
 * 
 * 适用于所有微服务的数据访问层测试
 * 使用 @DataJpaTest 只加载 JPA 相关组件
 */
@DataJpaTest
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @BeforeEach
    protected void setUp() {
        // 子类可覆盖进行初始化
    }
}

