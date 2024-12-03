package com.ecstel.sym.mapper.ccaas;

import com.ecstel.sym.utils.DataMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CCaaSDbMapper {


    List<Map<String, Object>> ExistsTableContact(DataMap paramMap);
    List<Map<String, Object>> ExistsTablePartition(DataMap paramMap);

    int AlterTablePartition(DataMap paramMap);
    int insertUservice(DataMap paramMap);


    int deleteStatHserviceData(DataMap paramMap);

    int deleteStatHAgtinboundData(DataMap paramMap);

    int deleteStatHAgentStateData(DataMap paramMap);

    int deleteStatHAgtoutboundData(DataMap paramMap);

    int deleteStatHAgtData(DataMap paramMap);
    int insertUcampaign(DataMap paramMap);

    int insertUAcw(DataMap paramMap);

    int insertUNrsn(DataMap paramMap);

    int insertUPoc(DataMap paramMap);

    int updateAgentServiceMap(DataMap paramMap);

    List<Map<String, Object>> selectEcontactMaxData(DataMap paramMap);

    List<Map<String, Object>> selectCInboundMaxData(DataMap paramMap);

    List<Map<String, Object>> selectCOutboundMaxData(DataMap paramMap);

    List<Map<String, Object>> selectCCampaignMaxData(DataMap paramMap);

    List<Map<String, Object>> selectStatHServiceMaxData(DataMap paramMap);

    List<Map<String, Object>> selectStatHAgentStateMaxData(DataMap paramMap);

    List<Map<String, Object>> selectStatHAgtMaxData(DataMap paramMap);

    List<Map<String, Object>> selectStatHAgtinboundMaxData(DataMap paramMap);
    List<Map<String, Object>> getAgentList(DataMap paramMap);

    List<Map<String, Object>> selectStatHAgtOutboundMaxData(DataMap paramMap);

    List<Map<String, Object>> selectHagentInboundData(DataMap paramMap);

    List<Map<String, Object>> selectHagentOutboundData(DataMap paramMap);

    List<Map<String, Object>> selectBatchCheck(DataMap paramMap);

    List<Map<String, Object>> selectAgnetExists(DataMap paramMap);

    List<Map<String, Object>> setlectUserviceData(DataMap paramMap);

    List<Map<String, Object>> getNrsnList(DataMap paramMap);

    List<Map<String, Object>> getServiceList(DataMap paramMap);


    int insertCInbound(DataMap paramMap);

    int insertCOutbound(DataMap paramMap);

    int insertCCampaign(DataMap paramMap);

    int deleteEcontactData(DataMap paramMap);

    int deleteCInboundData(DataMap paramMap);

    int deleteCOutboundData(DataMap paramMap);
    int deleteCCampaignData(DataMap paramMap);

    int insertEcontact(DataMap paramMap);

    int insertEcontactPds(DataMap paramMap);


    int deleteEAgentState(DataMap paramMap);

    int insertEagentState(DataMap paramMap);

    int insertStatHServiceI(DataMap paramMap);

    int insertStatHAgtI(DataMap paramMap);

    int insertStatHServiceSummary(DataMap paramMap);

    int insertStatHAgentStateI(DataMap paramMap);

    int insertStatHAgtInbound(DataMap paramMap);

    int insertStatHAgtOutbound(DataMap paramMap);

    int insertStatHAgtInboundData(DataMap paramMap);

    int insertStatHAgtOutboundData(DataMap paramMap);

    int insertStatHAgentSummary(DataMap paramMap);

    int setBatchCheck(DataMap paramMap);

}