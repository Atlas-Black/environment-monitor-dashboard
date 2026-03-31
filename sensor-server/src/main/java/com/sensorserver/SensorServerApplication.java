//启动类+服务逻辑
//主启动类，Spring Boot入口
//运行，则为启动整个后端服务
package com.sensorserver;

import org.springframework.boot.SpringApplication;
// 引入 Spring Boot 的启动器类 SpringApplication，后面会用它来启动整个应用上下文（ApplicationContext）。

import org.springframework.boot.autoconfigure.SpringBootApplication;
// 引入 @SpringBootApplication 注解，用来标记这是 Spring Boot 应用的主配置类。
// @SpringBootApplication 是一个组合注解（相当于同时包含 @Configuration、@EnableAutoConfiguration、@ComponentScan）。
// =================== 主应用类 ===================
@SpringBootApplication(scanBasePackages = "com.sensorserver")
// 标注这是 Spring Boot 的启动类。
// 作用（总结）：
// 1) 把这个类当作配置类（等同于 @Configuration）。
// 2) 启用 Spring Boot 的自动配置（@EnableAutoConfiguration）：根据 classpath、配置等自动创建常用组件（例如内嵌 Tomcat、数据源等）。
// 3) 启用组件扫描（@ComponentScan）：默认从该类所在包开始扫描带注解的组件（@Component、@Service、@Repository、@Controller 等）。
//
// scanBasePackages = "com.sensorserver"：显式指定组件扫描的根包为 com.sensorserver。
// - 如果当前主类就位于 com.sensorserver 包下，这个属性可以省略（默认就是从主类所在包扫描）。
// - 当主类不在你想扫描的包根时，或想显式限制扫描范围时，可以设置这个属性。

public class SensorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SensorServerApplication.class, args);
        // 启动 Spring Boot 应用。这个调用会执行一系列步骤（简要）：
        // 1) 创建并刷新 Spring 的应用上下文（ApplicationContext），也就是 Spring 容器。
        // 2) 根据 classpath 和配置启用自动配置（Auto-configuration），创建必要的 Bean。
        // 3) 扫描并注册带注解的组件（比如 @Component、@Controller、@Service、@Repository）。
        // 4) 如果是 web 应用，会启动内嵌的 web 服务器（例如 Tomcat）。
        // 5) 发布启动事件（ApplicationStarted、ApplicationReady 等）。
        //
        // 参数说明：
        // - SensorServerApplication.class：告诉 Spring 哪个类是主要配置类（也影响默认扫描包）。
        // - args：把 JVM 启动时传入的命令行参数传递给 SpringApplication。

        System.out.println("✅ Sensor Server 已启动成功！");
    }
}
