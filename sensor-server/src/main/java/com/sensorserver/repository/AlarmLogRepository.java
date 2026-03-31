package com.sensorserver.repository;

//JpaRepository 已经封装好了数据库增删改查的功能。
//有了它，你就可以在 Controller 里直接调用 repo.save(...) 来存储报警数据

import com.sensorserver.entity.AlarmLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmLogRepository extends JpaRepository<AlarmLog, Long> {
}