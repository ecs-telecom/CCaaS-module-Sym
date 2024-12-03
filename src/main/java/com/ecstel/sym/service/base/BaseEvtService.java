package com.ecstel.sym.service.base;

import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.vo.BatchInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseEvtService {

    @Autowired
    private CCaaSDbMapper CCaaSMapper;

    @Autowired
    private BatchInfo batchInfo;
    public void ExistsTable(String tableName){
        DataMap map = new DataMap();
        map.put("tableName",tableName);
        List<Map<String, Object>> list = CCaaSMapper.ExistsTableContact(map);
    }

    public void ExistsPartition(String yyyymm,String tableName){
        DataMap map = new DataMap();
        map.put("tableName",tableName);
        map.put("schema",batchInfo.getSchema());
        map.put("partitionName","p"+yyyymm);
        List<Map<String, Object>> list = CCaaSMapper.ExistsTablePartition(map);

        if(Integer.parseInt(list.get(0).get("cnt").toString())> 0 ){
        }else{
        }
    }
}
