package com.ecstel.sym.service.bp;

import com.ecstel.sym.httpClient.ApiService;
import com.ecstel.sym.service.RealTimeService;
import com.ecstel.sym.vo.BatchInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Component("bpRagtService")
public class RagtService implements RealTimeService {
    private static final Logger log = LoggerFactory.getLogger(com.ecstel.sym.service.nice.RagtService.class);

    @Autowired
    private ApiService apiService; // ApiService 주입

    @Autowired
    private BatchInfo batchInfo; // BatchInfo를 저장할 인스턴스 변수

    @Override
    public void getRealTimeAgt(BatchInfo batchInfo) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");
        // 인스턴스를 통해 메서드 호출
        apiService.makeApiCall("url", params, batchInfo); // 필요한 경우 batchInfo를 사용
    }

    @Override
    public void getRealTimeService(BatchInfo batchInfo) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        // 인스턴스를 통해 메서드 호출
        apiService.makeApiCall("url", params, batchInfo); // 필요한 경우 batchInfo를 사용
    }

    @Override
    public void getAgtServiceMap(BatchInfo batchInfo) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        // 인스턴스를 통해 메서드 호출
        apiService.makeApiCall("url", params, batchInfo); // 필요한 경우 batchInfo를 사용
    }

    @Override
    public void getRealTimeCampaign(BatchInfo batchInfo) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("param1", "value1");
        params.put("param2", "value2");

        // 인스턴스를 통해 메서드 호출
        apiService.makeApiCall("url", params, batchInfo); // 필요한 경우 batchInfo를 사용
    }



}