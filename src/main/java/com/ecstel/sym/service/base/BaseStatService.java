package com.ecstel.sym.service.base;

import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.vo.BatchInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseStatService {

    @Autowired
    private CCaaSDbMapper CCaaSMapper;

    @Autowired
    private BatchInfo batchInfo;
    public void ExistsTable(String tableName){
        DataMap map = new DataMap();
        map.put("tableName",tableName);
        List<Map<String, Object>> list = CCaaSMapper.ExistsTableContact(map);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(list);
    }

    public void ExistsPartition(String yyyymm,String tableName){
        DataMap map = new DataMap();
        map.put("tableName",tableName);
        map.put("schema",batchInfo.getSchema());
        map.put("partitionName","p"+yyyymm);
        List<Map<String, Object>> list = CCaaSMapper.ExistsTablePartition(map);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(list);

        if(Integer.parseInt(list.get(0).get("cnt").toString())> 0 ){
            System.out.println("인덱스 있음");
        }else{
            System.out.println("인덱스 없음");
            //CCaaSMapper.AlterTablePartition(map);
        }
    }
}
