package com.example.test.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * 微服务集成测试基类
 * 
 * 用于测试微服务之间的调用
 * 使用 WireMock 模拟其他微服务的响应
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseMicroserviceTest {

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    protected void setUp() {
        wireMockServer.resetAll();
    }

    @AfterEach
    protected void tearDown() {
        wireMockServer.resetAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.service.url", () -> "http://localhost:" + wireMockServer.port());
    }

    /**
     * 获取 WireMock 服务器端口
     */
    protected int getWireMockPort() {
        return wireMockServer.port();
    }

    /**
     * 获取 WireMock 服务器基础 URL
     */
    protected String getWireMockBaseUrl() {
        return "http://localhost:" + wireMockServer.port();
    }
}

