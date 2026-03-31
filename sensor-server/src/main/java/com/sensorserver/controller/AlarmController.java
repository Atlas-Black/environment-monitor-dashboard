package com.sensorserver.controller;

import com.sensorserver.entity.AlarmLog;
import com.sensorserver.repository.AlarmLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarm")
public class AlarmController {

    @Autowired
    private AlarmLogRepository alarmLogRepository;

    // ✅ 保存报警信息
    @PostMapping("/add")
    public String addAlarm(@RequestBody AlarmLog alarm) {
        alarmLogRepository.save(alarm);
        System.out.println("🚨 保存报警日志：" + alarm.getMessage());
        return "✅ 报警已保存";
    }

    // ✅ 获取所有报警日志（或限制数量）
    @GetMapping("/list")
    public List<AlarmLog> getAlarms() {
        List<AlarmLog> all = alarmLogRepository.findAll();
        // 只返回最近 20 条（如果太多可以分页）
        if (all.size() > 20) {
            all = all.subList(all.size() - 20, all.size());
        }
        return all;
    }
}