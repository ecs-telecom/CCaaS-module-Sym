package com.ecstel.sym.service;

import com.ecstel.sym.vo.ServiceInfo;
import com.ecstel.sym.vo.ServiceInfoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ServiceInfoManager {

    @Autowired
    private ServiceInfoList serviceInfoList;

    // 데이터 추가

    public void addServiceInfo(Map<String, Object> serviceInfo) {
        serviceInfoList.addServiceInfo(serviceInfo);
    }



    // 리스트 데이터 조회 및 출력
    public List<Map<String, Object>> getServiceInfoList() {
        return serviceInfoList.getServiceInfoList();
    }

    public void clearServiceInfoList() {
        serviceInfoList.clearServiceInfoList();
    }

}