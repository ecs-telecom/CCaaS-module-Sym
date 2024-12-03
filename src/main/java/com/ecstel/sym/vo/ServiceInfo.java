package com.ecstel.sym.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data // Lombok의 @Data 애노테이션 사용
@Component // Spring 컴포넌트로 등록
public class ServiceInfo implements Serializable {
    private static final long serialVersionUID = 1L; // 직렬화 버전 UID
    private int companyId;
    private int tenantId;
    private String type;
    private String direction;
    private String ctiCode;
    private String ctiCodeName;
    private String label;
}