package com.sensorserver.analysis;

import com.sensorserver.model.SensorData;
import com.sensorserver.repository.SensorRepository;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final SensorRepository sensorRepository;

    public AnalysisService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Map<String, Object> analyzeTemperature() throws IOException {
        List<SensorData> dataList = sensorRepository.findAll();

        // 数据清洗
        List<SensorData> cleanedData = dataList.stream()
                .filter(d -> d.getTemperature() >= -10 && d.getTemperature() <= 50)
                .collect(Collectors.toList());

        // 统计分析
        DoubleSummaryStatistics stats = cleanedData.stream()
                .mapToDouble(SensorData::getTemperature)
                .summaryStatistics();

        double mean = stats.getAverage();
        double std = Math.sqrt(cleanedData.stream()
                .mapToDouble(d -> Math.pow(d.getTemperature() - mean, 2))
                .average()
                .orElse(0.0));

        // 趋势预测
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < cleanedData.size(); i++) {
            regression.addData(i, cleanedData.get(i).getTemperature());
        }
        double nextPrediction = regression.predict(cleanedData.size());

        // 绘制折线图
        XYSeries series = new XYSeries("Temperature");
        XYSeries trendSeries = new XYSeries("Trend");
        for (int i = 0; i < cleanedData.size(); i++) {
            series.add(i, cleanedData.get(i).getTemperature());
            trendSeries.add(i, regression.predict(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(trendSeries);

        var chart = ChartFactory.createXYLineChart(
                "Temperature Trend",
                "Time",
                "Temperature",
                dataset
        );

        File chartFile = new File("temperature_trend.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("max", stats.getMax());
        result.put("min", stats.getMin());
        result.put("avg", mean);
        result.put("std", std);
        result.put("nextPrediction", nextPrediction);
        result.put("chartFile", chartFile.getAbsolutePath());

        return result;
    }
}