package com.ecstel.sym.service.nice;

import com.ecstel.sym.config.NiceApiUrlConfig;
import com.ecstel.sym.httpClient.ApiService;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.mapper.ecp.EcpDbMapper;
import com.ecstel.sym.service.RealTimeService;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.vo.BatchInfo;
import com.ecstel.sym.vo.ServiceInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component("niceInfoService")
@RequiredArgsConstructor
public class InfoService implements com.ecstel.sym.service.InfoService {

    private static final Logger log = LoggerFactory.getLogger(InfoService.class);

    @Autowired
    private ApiService apiService; // ApiService 주입

    @Autowired
    private NiceApiUrlConfig NiceApiUrlConfig; // ApiService 주입

    private final CCaaSDbMapper CCaaSMapper;
    private final EcpDbMapper EcpMapper;

    @Override
    @Transactional
    public void getU_Service(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getU_SERVICE(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").toString());
            if( totalRecords > 0){
                JsonNode skillsNode = rtn.path("skills");

                DataMap paramMap = new DataMap();
                // skills 반복 처리
                if (skillsNode.isArray()) {

                    paramMap = paramMap.makeParameterMap(skillsNode,batchInfo);

                    CCaaSMapper.insertUservice(paramMap);

                    /*for (JsonNode skillNode : skillsNode) {
                        int skillId = skillNode.path("skillId").asInt();
                        String skillName = skillNode.path("skillName").asText();
                        String mediaTypeName = skillNode.path("mediaTypeName").asText();
                        boolean isActive = skillNode.path("isActive").asBoolean();
                    }
                    */
                }
            }
        } else {
            log.info("getU_Service service API response is null.");
        }
    }

    @Override
    public void getU_Campagin(BatchInfo batchInfo) throws Exception {
        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getU_CAMPAIGNS(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("resultSet").get("totalRecords").asText());
            if( totalRecords > 0){
                JsonNode campaignsNode = rtn.path("campaigns");
                DataMap paramMap = new DataMap();
                // skills 반복 처리
                if (campaignsNode.isArray()) {
                    paramMap = paramMap.makeParameterMap(campaignsNode,batchInfo);
                    CCaaSMapper.insertUcampaign(paramMap);
                }
            }
        } else {
            log.info("getU_Campagin service API response is null.");
        }
    }

    @Override
    @Transactional
    public void getU_Acw(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();

        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getU_ACW(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
            if( totalRecords > 0){
                JsonNode dispositionsNode = rtn.path("dispositions");
                DataMap paramMap = new DataMap();
                if (dispositionsNode.isArray()) {
                    paramMap = paramMap.makeParameterMap(dispositionsNode,batchInfo);
                    CCaaSMapper.insertUAcw(paramMap);
                }
            }
        } else {
            log.info("getU_Acw service API response is null.");
        }
    }

    @Override
    public void getU_Agent(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getU_AGENT(), params, batchInfo); // 필요한 경우 batchInfo를 사용
        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
            if( totalRecords > 0){
                List<DataMap> items = new ArrayList<>();
                JsonNode agentList = rtn.path("agents");

                for (JsonNode item : agentList) {
                    DataMap itemMap = new DataMap();
                    itemMap.put("CCT_CTI_ID",item.get("agentId").asInt());
                    items.add(itemMap);
                }

                DataMap contactList = new DataMap();
                contactList.put("list",items);
                List<Map<String, Object>> agentLists = CCaaSMapper.selectAgnetExists(contactList);

                items = new ArrayList<>();
                for (JsonNode item : agentList) {
                    DataMap itemMap = new DataMap();
                    int agentId = item.get("agentId").asInt();
                    String agentName = item.get("lastName").asText() + " " + item.get("firstName").asText();
                    String userName = item.get("userName").asText();
                    String emailAddress = item.get("emailAddress").asText();
                    int voiceThreshold = item.get("voiceThreshold").asInt();
                    int agentChatThreshold = item.get("agentChatThreshold").asInt();
                    int DigitalThreshold = item.get("DigitalThreshold").asInt();
                    int agentEmailThreshold = item.get("agentEmailThreshold").asInt();
                    int agentWorkItemThreshold = item.get("agentWorkItemThreshold").asInt();
                    int agentTotalContactCount = item.get("agentTotalContactCount").asInt();
                    int isActive = item.get("isActive").asBoolean() ? 1 : 0;
                    int isExists = -1;
                    for (Map<String, Object> agent : agentLists) {
                        int agtId = (Integer) agent.get("agentId"); // 언박싱
                        String existsflag = (String) agent.get("existsflag"); // 언박싱

                        if(agentId == agtId){
                            isExists = existsflag.equals("O")? 1 : 0;
                            break;
                        }
                    }


                    itemMap.put("isActive",isActive);
                    itemMap.put("teamId",0);
                    itemMap.put("voiceThreshold",voiceThreshold);
                    itemMap.put("agentChatThreshold",agentChatThreshold);
                    itemMap.put("digitalthreshold",DigitalThreshold);
                    itemMap.put("agentEmailThreshold",agentEmailThreshold);
                    itemMap.put("agentWorkItemThreshold",agentWorkItemThreshold);
                    itemMap.put("agentTotalContactCount",agentTotalContactCount);
                    itemMap.put("agentId",agentId);
                    itemMap.put("companyId",batchInfo.getCompanyId());
                    itemMap.put("agentName",agentName);
                    itemMap.put("userName",userName);
                    itemMap.put("emailAddress",emailAddress);
                    itemMap.put("isExists",isExists);

                    items.add(itemMap);
                }

                DataMap agenttList = new DataMap();
                agenttList.put("list",items);
                EcpMapper.updateEcpAgent(agenttList);
            }
        } else {
            log.info("getU_Campagin service API response is null.");
        }
    }

    @Override
    @Transactional
    public void getU_Nrsn(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getU_NRSN(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
            if( totalRecords > 0){
                JsonNode unavailableCodesNode = rtn.path("unavailableCodes");
                DataMap paramMap = new DataMap();
                // skills 반복 처리
                if (unavailableCodesNode.isArray()) {
                    paramMap = paramMap.makeParameterMap(unavailableCodesNode,batchInfo);
                    CCaaSMapper.insertUNrsn(paramMap);
                }
            }
        } else {
            log.info("getU_Nrsn service API response is null.");
        }
    }

    @Override
    @Transactional
    public void getU_WorkingHour(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall("/incontactapi/services/v30.0/campaigns", params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("resultSet").get("totalRecords").asText());
            if( totalRecords > 0){
                JsonNode campaignsNode = rtn.path("campaigns");
                DataMap paramMap = new DataMap();
                // skills 반복 처리
                if (campaignsNode.isArray()) {

                    paramMap = paramMap.makeParameterMap(campaignsNode,batchInfo);
                    CCaaSMapper.insertUcampaign(paramMap);
                }
            }
        } else {
            log.info("getU_WorkingHour service API response is null.");
        }
    }

    @Override
    @Transactional
    public void getU_Poc(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getU_POC(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
            if( totalRecords > 0){
                JsonNode pointsOfContactNode = rtn.path("pointsOfContact");
                DataMap paramMap = new DataMap();
                // skills 반복 처리
                if (pointsOfContactNode.isArray()) {
                    paramMap = paramMap.makeParameterMap(pointsOfContactNode,batchInfo);
                    CCaaSMapper.insertUPoc(paramMap);
                }
            }
        } else {
            log.info("getU_Poc service API response is null.");
        }
    }
}
