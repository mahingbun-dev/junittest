package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot 应用启动测试
 * 
 * 测试颗粒度：冒烟测试 (Smoke Test)
 * 测试目标：验证 Spring 应用上下文能够正确加载
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("应用启动测试")
class ApplicationTests {

    @Test
    @DisplayName("Spring 应用上下文加载成功")
    void contextLoads() {
        // 如果 Spring 上下文无法加载，此测试会失败
    }
}

