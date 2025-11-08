package com.sensorserver.entity;
//对应建的alarm_log数据表，每次保存报警信息，Spring Data JPA自动把对象转成数据库记录

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_log")
public class AlarmLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String level;

    private LocalDateTime timestamp = LocalDateTime.now();

    // ===== Getter 和 Setter =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}