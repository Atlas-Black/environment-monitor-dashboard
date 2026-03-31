package com.sensorserver.repository;
//数据访问层


import com.sensorserver.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SensorRepository extends JpaRepository<SensorData, Long> {
}