package com.ecstel.sym.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nice.url")
@Data
public class NiceApiUrlConfig {
    private String U_SERVICE;
    private String U_CAMPAIGNS;
    private String U_ACW;
    private String U_AGENT;
    private String U_NRSN;
    private String U_POC;
    private String R_SERVICE;
    private String R_AGENT;
    private String E_CONTACT;
    private String E_AGENT_STATUS;
    private String ECP_AGENT_SERVICE_MAP;
}