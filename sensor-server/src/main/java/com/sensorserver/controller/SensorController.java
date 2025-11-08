//控制层，接收请求
package com.controller; // 请改成你项目实际包名，例如 com.example.sensors.controller

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.model.SensorData;       // 对应你的实体类包路径
import com.repository.SensorRepository; // 如果有持久化的话

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class SensorController {

    private static final Logger logger = LoggerFactory.getLogger(SensorController.class);

    @Autowired(required = false)
    private SensorRepository sensorRepository; // 如果没有 repository 可以删掉或标注为 optional

    @PostMapping("/api/sensors")
    public ResponseEntity<String> receiveSensorData(@RequestBody SensorData data) {
        // 控制台输出（也会进 Spring 日志）
        logger.info("📥 接收到传感器数据: {}", data);

        // 如果你想同时在 System.out 打印（方便在 IDEA 控制台直接看）
        System.out.println("📥 接收到传感器数据: " + data);

        // 如果配置了 repository 并且实体映射没问题，可以保存
        if (sensorRepository != null) {
            try {
                sensorRepository.save(data);
                logger.info("💾 数据已保存到数据库: {}", data);
            } catch (Exception e) {
                logger.error("保存数据失败: ", e);
            }
        }

        return ResponseEntity.ok("Data received");
    }
}
