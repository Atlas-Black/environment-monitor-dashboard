package com.sensorserver.analysis;

import com.sensorserver.model.SensorData;
import com.sensorserver.repository.SensorRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private SensorRepository sensorRepository;

    /**
     * 获取实时数据 + 模拟预测数据
     */
    @GetMapping("/latest")
    public Map<String, Object> getLatestAnalysis() {
        List<SensorData> allData = sensorRepository.findAll();

        Map<String, Object> result = new HashMap<>();
        result.put("allData", allData);

        // ===== 模拟预测趋势（10个未来值） =====
        List<Double> trendData = new ArrayList<>();
        if (!allData.isEmpty()) {
            SensorData last = allData.get(allData.size() - 1);
            double temp = last.getTemperature();
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                temp += rand.nextDouble() * 1.5; // 模拟微小上升
                trendData.add(Math.round(temp * 10.0) / 10.0);
            }
        }

        result.put("trendData", trendData);
        return result;
    }
}