package com.example.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web MVC 测试基类
 * 
 * 适用场景：
 * - 测试 Controller 层
 * - 不需要启动完整服务器
 * - 模拟 HTTP 请求和响应
 * 
 * 特点：
 * - 只加载 Web 层组件
 * - 使用 MockMvc 进行请求模拟
 * - 执行速度较快
 */
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseWebMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * 每个测试方法执行前的初始化
     */
    @BeforeEach
    protected void setUp() {
        // 默认实现为空，子类可覆盖
    }

    /**
     * 将对象转换为 JSON 字符串
     */
    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 将 JSON 字符串转换为对象
     */
    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }
}

