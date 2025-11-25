package com.example.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * 架构测试
 * 
 * 测试颗粒度：架构测试 (Architecture Test)
 * 测试目标：验证代码架构是否符合设计规范
 * 测试策略：使用 ArchUnit 进行架构约束验证
 */
@DisplayName("架构测试")
class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.example");
    }

    // ==================== 分层架构测试 ====================

    @Nested
    @DisplayName("分层架构测试")
    class LayeredArchitectureTests {

        @Test
        @DisplayName("验证分层架构依赖规则")
        void layeredArchitectureShouldBeRespected() {
            ArchRule rule = layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Controller").definedBy("..controller..")
                    .layer("Service").definedBy("..service..")
                    .layer("Repository").definedBy("..repository..")
                    .layer("Entity").definedBy("..entity..")
                    .layer("DTO").definedBy("..dto..")
                    .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                    .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                    .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

            rule.check(importedClasses);
        }
    }

    // ==================== 命名规范测试 ====================

    @Nested
    @DisplayName("命名规范测试")
    class NamingConventionTests {

        @Test
        @DisplayName("Controller 类应该以 Controller 结尾")
        void controllersShouldBeSuffixedWithController() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..controller..")
                    .and().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().haveSimpleNameEndingWith("Controller");

            rule.check(importedClasses);
        }

        @Test
        @DisplayName("Service 实现类应该以 Impl 结尾")
        void serviceImplementationsShouldBeSuffixedWithImpl() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..service.impl..")
                    .should().haveSimpleNameEndingWith("Impl");

            rule.check(importedClasses);
        }

        @Test
        @DisplayName("Repository 接口应该以 Repository 结尾")
        void repositoriesShouldBeSuffixedWithRepository() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..repository..")
                    .and().areInterfaces()
                    .should().haveSimpleNameEndingWith("Repository");

            rule.check(importedClasses);
        }
    }

    // ==================== 注解使用测试 ====================

    @Nested
    @DisplayName("注解使用测试")
    class AnnotationTests {

        @Test
        @DisplayName("Controller 类应该使用 @RestController 注解")
        void controllersShouldBeAnnotatedWithRestController() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..controller..")
                    .and().haveSimpleNameEndingWith("Controller")
                    .and().areNotInterfaces()
                    .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class);

            rule.check(importedClasses);
        }

        @Test
        @DisplayName("Service 实现类应该使用 @Service 注解")
        void servicesShouldBeAnnotatedWithService() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..service.impl..")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

            rule.check(importedClasses);
        }

        @Test
        @DisplayName("Repository 接口应该使用 @Repository 注解")
        void repositoriesShouldBeAnnotatedWithRepository() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..repository..")
                    .and().areInterfaces()
                    .and().haveSimpleNameEndingWith("Repository")
                    .should().beAnnotatedWith(org.springframework.stereotype.Repository.class);

            rule.check(importedClasses);
        }
    }

    // ==================== 依赖规则测试 ====================

    @Nested
    @DisplayName("依赖规则测试")
    class DependencyTests {

        @Test
        @DisplayName("Controller 不应该直接访问 Repository")
        void controllersShouldNotAccessRepositories() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..");

            rule.check(importedClasses);
        }

        @Test
        @DisplayName("Entity 不应该依赖其他层")
        void entitiesShouldNotDependOnOtherLayers() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..entity..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..controller..",
                            "..service..",
                            "..repository.."
                    );

            rule.check(importedClasses);
        }
    }
}

