package com.ecstel.sym.vo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ServiceInfoList {
    private final List<Map<String, Object> > serviceInfoList = new ArrayList<>();

    // 리스트에 추가
    public void addServiceInfo(Map<String, Object> serviceInfo) {
        serviceInfoList.add(serviceInfo);
    }

    // 전체 리스트 반환
    public List<Map<String, Object>> getServiceInfoList() {
        return serviceInfoList;
    }

    // 리스트 초기화
    public void clearServiceInfoList() {
        serviceInfoList.clear();
    }
}