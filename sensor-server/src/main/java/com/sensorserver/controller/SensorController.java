//控制层，接收请求
package com.sensorserver.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import com.sensorserver.model.SensorData;
import com.sensorserver.repository.SensorRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    @Autowired
    private SensorRepository sensorRepository;

    // 新增 RestTemplate，用于自动上报报警信息
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/data")
    public String receiveData(@RequestBody SensorData data) {
        data.setTimestamp(LocalDateTime.now());
        sensorRepository.save(data);

        System.out.printf("✅ 接收到数据 | ID:%s | 温度: %.2f°C | 湿度: %.2f%% | PM2.5: %.2f%n",
                data.getSensorId(), data.getTemperature(), data.getHumidity(), data.getPm25());

        // ======= 报警逻辑 =======
        if (data.getTemperature() > 35) {
            String msg = "🌡️ 高温警报：" + data.getTemperature() + "°C";
            System.out.println("⚠️ " + msg);
            sendAlarm(msg, "高温");
        }

        if (data.getPm25() > 75) {
            String msg = "🌫️ 空气污染警报：PM2.5=" + data.getPm25();
            System.out.println("⚠️ " + msg);
            sendAlarm(msg, "污染");
        }

        return "✅ 数据已接收并保存";
    }

    /**
     * 向 /api/alarm/add 自动上报报警信息
     */
    private void sendAlarm(String message, String level) {
        try {
            String url = "http://localhost:8080/api/alarm/add";
            Map<String, String> alarmData = new HashMap<>();
            alarmData.put("message", message);
            alarmData.put("level", level);

            restTemplate.postForObject(url, alarmData, String.class);
            System.out.println("📡 已上报报警信息到 /api/alarm/add");
        } catch (Exception e) {
            System.err.println("❌ 上报报警失败：" + e.getMessage());
        }
    }

    @GetMapping("/alerts")
    public List<String> getAlerts() {
        List<SensorData> allData = sensorRepository.findAll();
        List<String> alertLogs = new ArrayList<>();

        for (SensorData data : allData) {
            if (data.getTemperature() > 35) {
                alertLogs.add(data.getTimestamp() + " ⚠️ 高温警报：" + data.getTemperature() + "°C");
            }
            if (data.getPm25() > 75) {
                alertLogs.add(data.getTimestamp() + " ⚠️ 空气污染警报：PM2.5=" + data.getPm25());
            }
        }

        if (alertLogs.size() > 10) {
            alertLogs = alertLogs.subList(alertLogs.size() - 10, alertLogs.size());
        }
        return alertLogs;
    }

    @GetMapping("/data")
    public List<SensorData> getAllData() {
        return sensorRepository.findAll();
    }

    @GetMapping("/latest")
    public SensorData getLatestData() {
        List<SensorData> allData = sensorRepository.findAll();
        if (allData.isEmpty()) {
            SensorData empty = new SensorData();
            empty.setSensorId("N/A");
            empty.setTemperature(0.0);
            empty.setHumidity(0.0);
            empty.setPm25(0.0);
            return empty;
        }
        return allData.get(allData.size() - 1);
    }
}