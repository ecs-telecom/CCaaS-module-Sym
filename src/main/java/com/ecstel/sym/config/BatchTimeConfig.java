package com.ecstel.sym.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sym.job")
@Data
public class BatchTimeConfig {
    private String infoSchedule;  // 예: "0 0/15 * * * ?"
    private String statSchedule;   // 예: "0 5,20,35,50 * * * ?"
    private String evtSchedule;    // 예: "0 0/1 * * * ?"
    private String realSchedule;   // 예: "*/2 * * * * ?"
    private String dailySchedule; // 예: "0 0 1 * * ?"


}