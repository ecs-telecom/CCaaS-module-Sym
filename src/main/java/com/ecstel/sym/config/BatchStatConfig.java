package com.ecstel.sym.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sym.stat")
@Data
public class BatchStatConfig {
    private Boolean iflag;   // 15분 배치 동작여부
    private Boolean i30flag; // 30분 배치 동작여부
    private Boolean hflag;   // 시간 배치 동작여부
    private Boolean dflag;   // 일별 배치 동작여부
    private Boolean mflag;   // 월별 배치 동작여부
}