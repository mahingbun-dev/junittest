package com.example.test.mock;

import com.example.test.util.JsonTestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Query 微服务 Mock
 * 
 * 模拟 Query 查询服务的接口响应
 * 供其他微服务测试时使用
 */
public class QueryServiceMock {

    private static final String BASE_PATH = "/query/api";

    // ==================== 查询接口 Mock ====================

    /**
     * Mock 分页查询
     */
    public static void mockPageQuery(String resource, List<?> data, int total, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("content", data);
        pageData.put("total", total);
        pageData.put("page", page);
        pageData.put("size", size);
        pageData.put("totalPages", (total + size - 1) / size);
        response.put("data", pageData);

        stubFor(get(urlPathEqualTo(BASE_PATH + "/" + resource))
                .withQueryParam("page", equalTo(String.valueOf(page)))
                .withQueryParam("size", equalTo(String.valueOf(size)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 条件查询
     */
    public static void mockQueryByCondition(String resource, Map<String, String> conditions, List<?> data) {
        var stubMapping = get(urlPathEqualTo(BASE_PATH + "/" + resource + "/search"));
        
        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            stubMapping = stubMapping.withQueryParam(entry.getKey(), equalTo(entry.getValue()));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);

        stubFor(stubMapping.willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 聚合查询
     */
    public static void mockAggregateQuery(String resource, String aggregationType, Object result) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);

        stubFor(get(urlEqualTo(BASE_PATH + "/" + resource + "/aggregate/" + aggregationType))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 统计查询
     */
    public static void mockStatistics(String resource, Map<String, Object> statistics) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", statistics);

        stubFor(get(urlEqualTo(BASE_PATH + "/" + resource + "/statistics"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }

    /**
     * Mock 查询为空
     */
    public static void mockQueryEmpty(String resource) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", new Object[0]);

        stubFor(get(urlPathMatching(BASE_PATH + "/" + resource + ".*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JsonTestUtil.toJson(response))));
    }
}

