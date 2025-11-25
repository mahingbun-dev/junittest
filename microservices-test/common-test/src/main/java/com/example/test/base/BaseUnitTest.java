package com.example.test.base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 * 
 * 适用于所有微服务的纯单元测试
 * 特点：不启动 Spring 容器，使用 Mockito 进行依赖模拟
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    @BeforeEach
    protected void setUp() {
        // 子类可覆盖进行初始化
    }
}

