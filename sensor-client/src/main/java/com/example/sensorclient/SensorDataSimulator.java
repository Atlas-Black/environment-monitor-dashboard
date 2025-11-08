package com.example.sensorclient;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class SensorDataSimulator {

    // 修改为你的后端实际地址
    private static final String API_URL = "http://localhost:8080/api/sensor/data";
    private static final int SENSOR_COUNT = 3;
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("🌡️ 启动传感器数据模拟器...");

        for (int i = 1; i <= SENSOR_COUNT; i++) {
            int sensorId = i;
            new Thread(() -> sendSensorData(sensorId)).start();
        }
    }

    private static void sendSensorData(int sensorId) {
        while (true) {
            try {
                double temperature = 15 + random.nextDouble() * 15; // 15~30℃
                double humidity = 40 + random.nextDouble() * 50;    // 40~90%
                double pm25 = 10 + random.nextDouble() * 80;        // 10~90μg/m³

                String json = String.format(
                        "{\"sensorId\":\"Sensor-%d\", \"temperature\":%.2f, \"humidity\":%.2f, \"pm25\":%.2f}",
                        sensorId, temperature, humidity, pm25
                );

                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes("UTF-8"));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                System.out.printf("✅ [%s] 数据已发送: %s (响应码: %d)%n",
                        "Sensor-" + sensorId, json, responseCode);

                conn.disconnect();

                // 间隔 3~5 秒发送一次
                Thread.sleep(3000 + random.nextInt(2000));

            } catch (Exception e) {
                System.err.println("❌ Sensor-" + sensorId + " 数据发送失败: ");
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}