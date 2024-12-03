package com.ecstel.sym.service;

import com.ecstel.sym.mapper.ecp.EcpDbMapper;
import com.ecstel.sym.utils.DataMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class InitService {


    private final EcpDbMapper ecpDbMapper;


    public List<Map<String, Object>> loadConfigurationFromDB(DataMap datamap) {
        return ecpDbMapper.selectCompanyInfo(datamap); // 매퍼를 사용하여 DB에서 데이터 로드
    }

}
