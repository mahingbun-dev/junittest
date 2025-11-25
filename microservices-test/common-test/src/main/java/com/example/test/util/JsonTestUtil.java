package com.example.test.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * JSON 测试工具类
 * 
 * 提供 JSON 序列化和反序列化工具方法
 */
public class JsonTestUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 对象转 JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * 对象转格式化的 JSON 字符串
     */
    public static String toPrettyJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * JSON 字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * JSON 字符串转泛型对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * JSON 字符串转 Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String json) {
        return fromJson(json, Map.class);
    }

    /**
     * JSON 字符串转 List
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * 从资源文件读取 JSON
     */
    public static <T> T fromResource(String resourcePath, Class<T> clazz) {
        try (InputStream is = JsonTestUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("资源文件不存在: " + resourcePath);
            }
            return objectMapper.readValue(is, clazz);
        } catch (IOException e) {
            throw new RuntimeException("读取资源文件失败: " + resourcePath, e);
        }
    }

    /**
     * 获取 ObjectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

