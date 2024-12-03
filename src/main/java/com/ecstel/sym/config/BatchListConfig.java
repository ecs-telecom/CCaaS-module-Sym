package com.ecstel.sym.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sym.batchlist")
@Data
public class BatchListConfig {
    private Boolean U_SERVICE;
    private Boolean U_CAMPAIGNS;
    private Boolean U_ACW;
    private Boolean U_AGENT;
    private Boolean U_NRSN;
    private Boolean U_POC;
    private Boolean C_INBOUND;
    private Boolean C_OUTBOUND;
    private Boolean C_CAMPAIGN;
    private Boolean E_AGENT_STATUS;
    private Boolean E_CONTACT;
    private Boolean STAT_H_SERVICE;
    private Boolean R_SERVICE;
    private Boolean R_AGENT;
    private Boolean R_CAMPAIGN;
    private Boolean AGT_SERVICE_MAP;





}