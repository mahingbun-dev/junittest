package com.example.test.util;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 【测试数据工厂】
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 这个类是干什么的？
 * ═══════════════════════════════════════════════════════════════
 * 
 * 测试数据工厂是一个"生产测试数据的工厂"。
 * 
 * 为什么需要这个类？
 * - 测试需要大量的测试数据（用户名、邮箱、手机号等）
 * - 手动编写测试数据很麻烦："user1", "user2", "user3"...
 * - 相同的测试数据可能导致测试不够全面
 * - 随机数据能发现更多边界情况
 * 
 * 这个类提供：
 * - 随机数据生成（每次都不一样）
 * - 符合格式要求的数据（如有效的邮箱、手机号）
 * - 批量数据生成
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 Faker 是什么？
 * ═══════════════════════════════════════════════════════════════
 * 
 * Faker 是一个生成假数据的库。它可以生成：
 * - 姓名（真实的姓名格式）
 * - 地址（真实的地址格式）
 * - 邮箱（有效的邮箱格式）
 * - 公司名、电话号码、日期等等
 * 
 * 支持多种语言，我们用的是简体中文：
 * new Faker(Locale.SIMPLIFIED_CHINESE)
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 static 关键字说明
 * ═══════════════════════════════════════════════════════════════
 * 
 * 这个类的所有方法都是 static（静态的），这意味着：
 * - 不需要创建对象就能使用
 * - 直接通过类名调用：TestDataFactory.randomEmail()
 * - 所有调用共享同一个 Faker 实例（省内存）
 * 
 * 使用方式对比：
 * <pre>
 * // 静态方法（本类的方式）- 推荐
 * String email = TestDataFactory.randomEmail();
 * 
 * // 非静态方法（需要先创建对象）
 * TestDataFactory factory = new TestDataFactory();
 * String email = factory.randomEmail();  // 多此一举
 * </pre>
 * 
 * ═══════════════════════════════════════════════════════════════
 * 📚 使用示例
 * ═══════════════════════════════════════════════════════════════
 * <pre>
 * // 生成单个数据
 * Long id = TestDataFactory.randomId();
 * String username = TestDataFactory.randomUsername();
 * String email = TestDataFactory.randomEmail();
 * 
 * // 生成完整的测试对象
 * UserDTO user = UserDTO.builder()
 *         .id(TestDataFactory.randomId())
 *         .username(TestDataFactory.randomUsername())
 *         .email(TestDataFactory.randomEmail())
 *         .build();
 * 
 * // 批量生成
 * List<String> emails = new ArrayList<>();
 * for (int i = 0; i < 10; i++) {
 *     emails.add(TestDataFactory.randomEmail());
 * }
 * </pre>
 */
public class TestDataFactory {

    /**
     * Faker 实例 - 用于生成假数据
     * 
     * 【private static final 解释】
     * - private: 只能在这个类内部使用
     * - static: 属于类，只创建一个实例
     * - final: 创建后不能再指向其他对象（常量）
     * 
     * Locale.SIMPLIFIED_CHINESE 表示使用中文数据
     */
    private static final Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);

    /**
     * Random 实例 - 用于生成随机数
     */
    private static final Random random = new Random();

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    基础数据生成方法                         ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * 生成随机 ID（1 到 100万之间的正整数）
     * 
     * 为什么要 Math.abs()？
     * - random.nextLong() 可能返回负数
     * - ID 通常是正数，所以取绝对值
     * 
     * 为什么要 % 1000000 + 1？
     * - % 1000000 得到 0-999999 的数
     * - +1 确保结果至少是 1（避免 ID 为 0）
     * 
     * @return 1-1000000 之间的随机 Long
     */
    public static Long randomId() {
        return Math.abs(random.nextLong()) % 1000000 + 1;
    }

    /**
     * 生成随机用户名
     * 
     * 使用 Faker 生成，格式类似：zhangsan、lisi
     * 
     * @return 随机用户名字符串
     */
    public static String randomUsername() {
        return faker.name().username();
    }

    /**
     * 生成随机邮箱
     * 
     * 使用 Faker 生成，格式类似：zhangsan@example.com
     * 生成的邮箱格式是有效的（包含 @ 和域名）
     * 
     * @return 随机邮箱字符串
     */
    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    /**
     * 生成随机密码
     * 
     * @return 8-20位的随机密码
     * 
     * 参数说明：
     * - 8: 最小长度
     * - 20: 最大长度
     */
    public static String randomPassword() {
        return faker.internet().password(8, 20);
    }

    /**
     * 生成随机中国手机号
     * 
     * 中国手机号格式：1 + 第二位(3-8) + 9位数字
     * 例如：13812345678、15987654321
     * 
     * @return 11位手机号字符串
     */
    public static String randomPhone() {
        // "1" + (3到8之间的数字) + 9位随机数字
        return "1" + (3 + random.nextInt(6)) + faker.number().digits(9);
    }

    /**
     * 生成随机姓名（中文全名）
     * 
     * @return 例如："张三"、"李四"
     */
    public static String randomName() {
        return faker.name().fullName();
    }

    /**
     * 生成随机公司名
     * 
     * @return 例如："阿里巴巴科技有限公司"
     */
    public static String randomCompany() {
        return faker.company().name();
    }

    /**
     * 生成随机地址
     * 
     * @return 完整地址字符串
     */
    public static String randomAddress() {
        return faker.address().fullAddress();
    }

    /**
     * 生成随机 UUID
     * 
     * UUID 是通用唯一识别码，几乎不可能重复。
     * 格式：xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     * 例如：550e8400-e29b-41d4-a716-446655440000
     * 
     * @return UUID 字符串
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成过去一年内的随机日期时间
     * 
     * LocalDateTime 是 Java 8 引入的日期时间类
     * 
     * @return 当前时间往前推 0-365 天的随机时间
     */
    public static LocalDateTime randomDateTime() {
        // LocalDateTime.now() 获取当前时间
        // minusDays(n) 减去 n 天
        // random.nextInt(365) 生成 0-364 的随机数
        return LocalDateTime.now().minusDays(random.nextInt(365));
    }

    /**
     * 生成过去一年内的随机日期（不含时间）
     * 
     * @return 当前日期往前推 0-365 天的随机日期
     */
    public static LocalDate randomDate() {
        return LocalDate.now().minusDays(random.nextInt(365));
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    集合数据生成方法                         ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * 生成随机字符串列表
     * 
     * @param size 列表大小
     * @return 包含随机单词的列表
     * 
     * 【Java 泛型 List<String> 说明】
     * List 是一个接口，表示"有序的元素集合"
     * <String> 表示列表中存储的元素类型是 String
     */
    public static List<String> randomStringList(int size) {
        // 创建一个 ArrayList（List 的常用实现类）
        List<String> list = new ArrayList<>();
        
        // 循环添加元素
        for (int i = 0; i < size; i++) {
            // faker.lorem().word() 生成一个随机单词
            list.add(faker.lorem().word());
        }
        
        return list;
    }

    /**
     * 生成随机 ID 列表
     * 
     * @param size 列表大小
     * @return 包含随机 ID 的列表
     */
    public static List<Long> randomIdList(int size) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(randomId());
        }
        return list;
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                    业务数据生成方法                         ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * 生成随机金额（0.01 到 10000.00 之间）
     * 
     * @return 保留两位小数的金额
     * 
     * 计算逻辑：
     * 1. random.nextDouble() 生成 0.0-1.0 的小数
     * 2. * 1000000 放大
     * 3. Math.round() 四舍五入取整
     * 4. / 100.0 缩小，得到两位小数
     */
    public static Double randomAmount() {
        return Math.round(random.nextDouble() * 1000000) / 100.0;
    }

    /**
     * 生成随机状态码
     * 
     * @param options 可选的状态值，如果不传则返回 0-4 的随机数
     * @return 随机状态码
     * 
     * 【可变参数 int... 说明】
     * int... options 表示可以传入任意数量的 int 参数
     * 调用方式：
     * - randomStatus()         // 不传参数
     * - randomStatus(1, 2, 3)  // 传入多个参数
     * - randomStatus(new int[]{1, 2, 3})  // 传入数组
     */
    public static Integer randomStatus(int... options) {
        if (options.length == 0) {
            // 如果没有传入可选值，返回 0-4 的随机数
            return random.nextInt(5);
        }
        // 从可选值中随机选一个
        return options[random.nextInt(options.length)];
    }

    /**
     * 生成随机订单号
     * 
     * 格式：ORD + 时间戳 + 4位随机数
     * 例如：ORD17001234567891234
     * 
     * @return 订单号字符串
     */
    public static String randomOrderNo() {
        return "ORD" + System.currentTimeMillis() + faker.number().digits(4);
    }

    /**
     * 生成随机交易号
     * 
     * 格式：TXN + 时间戳 + 6位随机数
     * 
     * @return 交易号字符串
     */
    public static String randomTransactionNo() {
        return "TXN" + System.currentTimeMillis() + faker.number().digits(6);
    }

    // ╔═══════════════════════════════════════════════════════════╗
    // ║                        工具方法                            ║
    // ╚═══════════════════════════════════════════════════════════╝

    /**
     * 获取 Faker 实例
     * 
     * 如果需要使用 Faker 的其他功能，可以通过这个方法获取实例
     * 
     * @return Faker 实例
     */
    public static Faker getFaker() {
        return faker;
    }

    /**
     * 从数组中随机选择一个元素
     * 
     * @param options 可选项数组
     * @param <T> 泛型，表示元素类型
     * @return 随机选中的元素
     * 
     * 【@SafeVarargs 注解说明】
     * 这个注解告诉编译器"我知道这个可变参数泛型是安全的"
     * 可以抑制编译器的警告
     * 
     * 使用示例：
     * <pre>
     * String color = randomFrom("红", "黄", "蓝");
     * Integer num = randomFrom(1, 2, 3, 4, 5);
     * </pre>
     */
    @SafeVarargs
    public static <T> T randomFrom(T... options) {
        return options[random.nextInt(options.length)];
    }

    /**
     * 从列表中随机选择一个元素
     * 
     * @param list 列表
     * @param <T> 元素类型
     * @return 随机选中的元素
     * 
     * 使用示例：
     * <pre>
     * List<User> users = Arrays.asList(user1, user2, user3);
     * User randomUser = randomFrom(users);
     * </pre>
     */
    public static <T> T randomFrom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
