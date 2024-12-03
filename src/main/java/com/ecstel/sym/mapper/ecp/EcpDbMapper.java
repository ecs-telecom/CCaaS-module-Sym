package com.ecstel.sym.mapper.ecp;


import com.ecstel.sym.utils.DataMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface EcpDbMapper {
    List<Map<String, Object>> selectCompanyInfo(DataMap paramMap);

    int updateEcpAgent(DataMap paramMap);
}