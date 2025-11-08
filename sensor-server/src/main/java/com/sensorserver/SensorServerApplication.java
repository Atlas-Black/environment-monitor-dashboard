package com.server;

//启动类+服务逻辑
//主启动类，Spring Boot入口
//运行，则为启动整个后端服务

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SensorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SensorServerApplication.class, args);
        System.out.println("✅ Sensor Server 已启动成功！");
    }
}