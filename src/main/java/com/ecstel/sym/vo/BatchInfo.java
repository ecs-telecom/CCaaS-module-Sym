package com.ecstel.sym.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data // Lombok의 @Data 애노테이션 사용
@Component // Spring 컴포넌트로 등록
public class BatchInfo implements Serializable {
    private static final long serialVersionUID = 1L; // 직렬화 버전 UID

    private String companyCode;
    private int companyId;
    private String contactCenterType;
    private String ccTenantUrl;
    private String ccTeantId;
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String companyName;
    private Boolean iflag;
    private Boolean i30flag;
    private Boolean hflag;
    private Boolean dflag;
    private Boolean mflag;
    private String globalProductionUrl;
    private String schema;

    // 생성자, getter, setter 등은 @Data 애노테이션으로 자동 생성됨
}