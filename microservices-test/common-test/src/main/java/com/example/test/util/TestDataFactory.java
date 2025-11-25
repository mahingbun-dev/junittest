package com.example.test.util;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 测试数据工厂
 * 
 * 提供各种测试数据生成方法，供所有微服务使用
 */
public class TestDataFactory {

    private static final Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);
    private static final Random random = new Random();

    // ==================== 基础数据生成 ====================

    /**
     * 生成随机 ID
     */
    public static Long randomId() {
        return Math.abs(random.nextLong()) % 1000000 + 1;
    }

    /**
     * 生成随机用户名
     */
    public static String randomUsername() {
        return faker.name().username();
    }

    /**
     * 生成随机邮箱
     */
    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * 生成随机密码
     */
    public static String randomPassword() {
        return faker.internet().password(8, 20);
    }

    /**
     * 生成随机手机号
     */
    public static String randomPhone() {
        return "1" + (3 + random.nextInt(6)) + faker.number().digits(9);
    }

    /**
     * 生成随机姓名
     */
    public static String randomName() {
        return faker.name().fullName();
    }

    /**
     * 生成随机公司名
     */
    public static String randomCompany() {
        return faker.company().name();
    }

    /**
     * 生成随机地址
     */
    public static String randomAddress() {
        return faker.address().fullAddress();
    }

    /**
     * 生成随机 UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成随机日期时间
     */
    public static LocalDateTime randomDateTime() {
        return LocalDateTime.now().minusDays(random.nextInt(365));
    }

    /**
     * 生成随机日期
     */
    public static LocalDate randomDate() {
        return LocalDate.now().minusDays(random.nextInt(365));
    }

    // ==================== 集合数据生成 ====================

    /**
     * 生成随机字符串列表
     */
    public static List<String> randomStringList(int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(faker.lorem().word());
        }
        return list;
    }

    /**
     * 生成随机 ID 列表
     */
    public static List<Long> randomIdList(int size) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(randomId());
        }
        return list;
    }

    // ==================== 业务数据生成 ====================

    /**
     * 生成随机金额 (0.01 - 10000.00)
     */
    public static Double randomAmount() {
        return Math.round(random.nextDouble() * 1000000) / 100.0;
    }

    /**
     * 生成随机状态码
     */
    public static Integer randomStatus(int... options) {
        if (options.length == 0) {
            return random.nextInt(5);
        }
        return options[random.nextInt(options.length)];
    }

    /**
     * 生成随机订单号
     */
    public static String randomOrderNo() {
        return "ORD" + System.currentTimeMillis() + faker.number().digits(4);
    }

    /**
     * 生成随机交易号
     */
    public static String randomTransactionNo() {
        return "TXN" + System.currentTimeMillis() + faker.number().digits(6);
    }

    // ==================== 工具方法 ====================

    /**
     * 获取 Faker 实例
     */
    public static Faker getFaker() {
        return faker;
    }

    /**
     * 从数组中随机选择一个元素
     */
    @SafeVarargs
    public static <T> T randomFrom(T... options) {
        return options[random.nextInt(options.length)];
    }

    /**
     * 从列表中随机选择一个元素
     */
    public static <T> T randomFrom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}

