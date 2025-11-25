package com.example.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 * 
 * 适用场景：
 * - 纯单元测试，不启动 Spring 容器
 * - 使用 Mockito 进行依赖模拟
 * - 测试单个类的业务逻辑
 * 
 * 特点：
 * - 执行速度快
 * - 隔离性好
 * - 不依赖外部资源
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    /**
     * 每个测试方法执行前的初始化
     * 子类可以覆盖此方法进行额外初始化
     */
    @BeforeEach
    protected void setUp() {
        // 默认实现为空，子类可覆盖
    }
}

