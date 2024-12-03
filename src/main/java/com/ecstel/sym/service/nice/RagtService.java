package com.ecstel.sym.service.nice;

import com.ecstel.sym.collection.realTimeAgent;
import com.ecstel.sym.collection.realTimeService;
import com.ecstel.sym.config.NiceApiUrlConfig;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.mapper.ecp.EcpDbMapper;
import com.ecstel.sym.service.MonggoService;
import com.ecstel.sym.service.RealTimeService;
import com.ecstel.sym.httpClient.ApiService;
import com.ecstel.sym.service.ServiceInfoManager;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.utils.DateUtils;
import com.ecstel.sym.vo.BatchInfo;
import com.ecstel.sym.vo.ServiceInfo;
import com.ecstel.sym.vo.ServiceInfoList;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Component("niceRagtService")
@RequiredArgsConstructor
public class RagtService implements RealTimeService {
    private static final Logger log = LoggerFactory.getLogger(RagtService.class);

    @Autowired
    private ApiService apiService; // ApiService 주입

    @Autowired
    private NiceApiUrlConfig NiceApiUrlConfig; // ApiService 주입

    @Autowired
    private MonggoService mongoService;

    @Autowired
    private ServiceInfoManager serviceInfoManager;


    private final CCaaSDbMapper CCaaSMapper;


    private boolean isValidAgent(JsonNode agentNode) {
        // 유효성 검사 (예: 상태가 "ready" 또는 "busy")
        String state = agentNode.path("agentStateName").asText();
        return !"LoggedOut".equals(state);
    }

    private realTimeAgent mapToAgent(JsonNode item, List<Map<String, Object>> nrsnList, List<Map<String, Object>> serviceList,List<Map<String, Object>> agentList) {
        try {
            int outStateId = item.path("outStateId").asInt();
            int skillId = item.path("skillId").asInt();
            int agentId = item.path("agentId").asInt();
            Long stateChangedTime = DateUtils.calculateTimeDifferenceInSeconds(DateUtils.formatToTimestamp6Plus9Hours(item.get("lastUpdateTime").asText()));
            Date currentDate = new Date();
            long nineHoursInMillis = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
            Date koreaTime = new Date(currentDate.getTime() + nineHoursInMillis);
            Timestamp timestamp = new Timestamp(koreaTime.getTime());

            Map<String, Object> nrsnInfo = nrsnList.stream()
                    .filter(nrsn -> nrsn.get("ctiCode").equals(outStateId))
                    .findFirst()
                    .orElse(null);

            Map<String, Object> serviceInfo = serviceList.stream()
                    .filter(service -> service.get("ctiCode").equals(skillId))
                    .findFirst()
                    .orElse(null);

            Map<String, Object> agentInfo = agentList.stream()
                    .filter(agent -> Integer.valueOf(agent.get("agtid").toString()) == agentId)
                    .findFirst()
                    .orElse(null);
            /*
            Long stateChangedTime = DateUtils.calculateTimeDifferenceInSeconds(DateUtils.formatToTimestamp6Plus9Hours(item.get("lastUpdateTime").asText()));
            agent.setSTATE_DURATION(stateChangedTime);
            agent.setANS(Integer.valueOf(agentInfo.get("ans").toString()));
            agent.setABD(Integer.valueOf(agentInfo.get("abd").toString()));
            agent.setOBCONN(Integer.valueOf(agentInfo.get("obconn").toString()));
            agent.setLOGIN_TIME(Integer.valueOf(agentInfo.get("logintime").toString()));
            agent.setTALKTIME_IN(Integer.valueOf(agentInfo.get("talktimeIn").toString()));
            agent.setTALKTIME_OUT(Integer.valueOf(agentInfo.get("talktimeOut").toString()));
            agent.setNOTREADY_TIME(Integer.valueOf(agentInfo.get("notreadytitme").toString()));
            agent.setAVGTALK_TIME(0.0);
            agent.setREADY_TIME(Integer.valueOf(agentInfo.get("readytime").toString()));
            */
            return new realTimeAgent(
                    timestamp,
                    agentInfo.get("companyId").toString() + agentInfo.get("agtid").toString(),
                    Integer.valueOf(agentInfo.get("companyId").toString()),
                    item.get("contactId").asInt(),
                    item.get("contactStartHandleTime").asText(),
                    item.get("isOutbound").asBoolean(),
                    item.get("fromAddress").asText(),
                    item.get("lastUpdateTime").asText(),
                    item.get("outStateDescription").asText(),
                    item.get("outStateId").asText(),
                    item.get("sessionStartTime").asText(),
                    item.get("skillId").asInt(),
                    item.get("skillId").asInt() != 0 ? String.valueOf(serviceInfo.get("label")) : "",
                    item.get("startDate").asText(),
                    String.valueOf(agentInfo.get("teamId")),
                    Integer.valueOf(agentInfo.get("agtid").toString()),
                    String.valueOf(agentInfo.get("name")),
                    "WebRTC",
                    item.get("agentStateName").asText(),
                    item.get("outStateDescription").hasNonNull("outStateDescription") ? item.get("outStateDescription").asText() : item.get("outStateId").asText(),
                    item.get("skillId").asInt(),
                    item.get("agentStateName").asText().equals("OutboundContact") ? "OUT" : item.get("agentStateName").asText().equals("InboundContact") ? "IN" : "",
                    nrsnInfo != null ? String.valueOf(nrsnInfo.get("label")) : "",
                    stateChangedTime,
                    Integer.valueOf(agentInfo.get("ans").toString()),
                    Integer.valueOf(agentInfo.get("abd").toString()),
                    Integer.valueOf(agentInfo.get("obconn").toString()),
                    Integer.valueOf(agentInfo.get("logintime").toString()),
                    Integer.valueOf(agentInfo.get("talktimeIn").toString()),
                    Integer.valueOf(agentInfo.get("talktimeOut").toString()),
                    Integer.valueOf(agentInfo.get("notreadytitme").toString()),
                    0.0,
                    Integer.valueOf(agentInfo.get("readytime").toString())
            );
        } catch (Exception e) {
            // 매핑 실패 시 null 반환
            return null;
        }
    }


    @Override
    public void getRealTimeAgt(BatchInfo batchInfo) throws Exception {





        DataMap param = new DataMap();
        param.put("companyId",batchInfo.getCompanyId());
        List<Map<String, Object>> nrsnList = CCaaSMapper.getNrsnList(param);
        List<Map<String, Object>> serviceList = CCaaSMapper.getServiceList(param);

        Map<String, String> params = new HashMap<>();
        JsonNode apiResponse = apiService.makeApiCall(NiceApiUrlConfig.getR_AGENT(), new HashMap<>(), batchInfo);

        List<DataMap> items = new ArrayList<>();
        JsonNode agentStatesList = apiResponse.path("agentStates");
        for (JsonNode item : agentStatesList) {
            DataMap itemMap = new DataMap();
            if (item.has("agentId") && !item.get("agentStateName").asText().equals("LoggedOut")) {
                itemMap.put("agentId",item.get("agentId").asInt());
                itemMap.put("companyId",batchInfo.getCompanyId());
                items.add(itemMap);
            }
        }
        DataMap contactList = new DataMap();
        contactList.put("list",items);
        List<Map<String, Object>> agentInfos = CCaaSMapper.getAgentList(contactList);


        if (apiResponse != null && apiResponse.has("agentStates")) {
            Iterator<JsonNode> agentStatesIterator = apiResponse.get("agentStates").elements();
            List<JsonNode> agentStates = new ArrayList<>();
            agentStatesIterator.forEachRemaining(agentStates::add);

            // 3. 유효한 데이터 필터링 및 매핑
            List<realTimeAgent> agents = agentStates.stream()
                    .filter(item -> isValidAgent(item))
                    .map(item -> mapToAgent(item, nrsnList, serviceList,agentInfos))
                    .filter(Objects::nonNull) // 매핑 실패 데이터 제거
                    .collect(Collectors.toList());

            // 4. MongoDB에 Bulk Write 저장
            /*if (!agents.isEmpty()) {
                mongoService.saveAgentsBulk(agents);
            }
             */
            if (!agents.isEmpty()) {
                for (realTimeAgent agent : agents) {
                    mongoService.saveAgent(agent); // 각각의 agent를 저장
                }
            }
        }







        /*


        if (rtn != null) {
            JsonNode agentStatesList = rtn.path("agentStates");

            List<DataMap> items = new ArrayList<>();
            for (JsonNode item : agentStatesList) {
                DataMap itemMap = new DataMap();
                if (item.has("agentId") && !item.get("agentStateName").asText().equals("LoggedOut")) {
                    itemMap.put("agentId",item.get("agentId").asInt());
                    itemMap.put("companyId",batchInfo.getCompanyId());
                    items.add(itemMap);
                }
            }

            if(items.size() > 0){
                DataMap contactList = new DataMap();
                contactList.put("list",items);
                List<Map<String, Object>> agentInfos = CCaaSMapper.getAgentList(contactList);

                Date currentDate = new Date();
                long nineHoursInMillis = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
                Date koreaTime = new Date(currentDate.getTime() + nineHoursInMillis);
                Timestamp timestamp = new Timestamp(koreaTime.getTime());

                for (JsonNode item : agentStatesList) {
                    if (item.has("agentId") && !item.get("agentStateName").asText().equals("LoggedOut")) {
                        realTimeAgent agent = new realTimeAgent();
                        Map<String, Object> agentInfo = agentInfos.stream()
                                .filter(agentItem -> Integer.valueOf(agentItem.get("agtid").toString()).equals(item.get("agentId").asInt()))
                                .findFirst()
                                .orElse(null); // 첫 번째 매칭된 값이 없으면 null 반환

                        Map<String, Object> nrsnInfo = nrsnList.stream()
                                .filter(nrsnItem -> Integer.valueOf(nrsnItem.get("ctiCode").toString()) == (item.get("outStateId").asInt()))
                                .findFirst()
                                .orElse(null); // 첫 번째 매칭된 값이 없으면 null 반환

                        Map<String, Object> serviceInfo = serviceList.stream()
                                .filter(serviceItem -> Integer.valueOf(serviceItem.get("ctiCode").toString()) == (item.get("skillId").asInt()))
                                .findFirst()
                                .orElse(null); // 첫 번째 매칭된 값이 없으면 null 반환



                        agent.setEvent("agent");
                        agent.setTimestamp(timestamp);
                        agent.setId(agentInfo.get("companyId").toString() + agentInfo.get("agtid").toString());
                        agent.setCompanyId(Integer.valueOf(agentInfo.get("companyId").toString()));
                        agent.setAgentStateId(item.get("agentStateId").asInt());
                        agent.setAgentstateName(item.get("agentStateName").asText());
                        agent.setContactId(item.get("contactId").asInt());
                        agent.setContactStartHandleTime(item.get("contactStartHandleTime").asText());
                        agent.setIsOutbound(item.get("isOutbound").asBoolean());
                        agent.setFromAddress(item.get("fromAddress").asText());
                        agent.setLastUpdateTime(item.get("lastUpdateTime").asText());
                        agent.setOutStateDescription(item.get("outStateDescription").asText());
                        agent.setOutStateId(item.get("outStateId").asText());//????????????????????????????int아님?
                        agent.setSessionStartTime(item.get("sessionStartTime").asText());
                        agent.setSkillId(item.get("skillId").asInt());
                        agent.setSkillName(item.get("skillId").asInt() != 0 ? String.valueOf(serviceInfo.get("label")) : "");
                        agent.setStartDate(item.get("startDate").asText());
                        agent.setTEAM_NAME(String.valueOf(agentInfo.get("teamId")));
                        agent.setLOGIN_ID(Integer.valueOf(agentInfo.get("agtid").toString()));
                        agent.setAGT_NAME(String.valueOf(agentInfo.get("name")));
                        agent.setEXTENSION("WebRTC");
                        agent.setACD_STATE(item.get("agentStateName").asText());
                        agent.setSUB_STATE(item.get("outStateDescription").hasNonNull("outStateDescription") ? item.get("outStateDescription").asText() : item.get("outStateId").asText());
                        agent.setACD_SKILL(item.get("skillId").asInt());
                        agent.setDIRECTION(item.get("agentStateName").asText().equals("OutboundContact") ? "OUT" : item.get("agentStateName").asText().equals("InboundContact") ? "IN" : "");
                        agent.setCALL_SUB_STATE(nrsnInfo != null ? String.valueOf(nrsnInfo.get("label")) : "" );
                        Long stateChangedTime = DateUtils.calculateTimeDifferenceInSeconds(DateUtils.formatToTimestamp6Plus9Hours(item.get("lastUpdateTime").asText()));
                        agent.setSTATE_DURATION(stateChangedTime);
                        agent.setANS(Integer.valueOf(agentInfo.get("ans").toString()));
                        agent.setABD(Integer.valueOf(agentInfo.get("abd").toString()));
                        agent.setOBCONN(Integer.valueOf(agentInfo.get("obconn").toString()));
                        agent.setLOGIN_TIME(Integer.valueOf(agentInfo.get("logintime").toString()));
                        agent.setTALKTIME_IN(Integer.valueOf(agentInfo.get("talktimeIn").toString()));
                        agent.setTALKTIME_OUT(Integer.valueOf(agentInfo.get("talktimeOut").toString()));
                        agent.setNOTREADY_TIME(Integer.valueOf(agentInfo.get("notreadytitme").toString()));
                        agent.setAVGTALK_TIME(0.0);
                        agent.setREADY_TIME(Integer.valueOf(agentInfo.get("readytime").toString()));


                        monggoService.saveAgent(agent);

                    }
                }
            }
        }

        */
    }

    @Override
    @Transactional
    public void getRealTimeService(BatchInfo batchInfo) throws Exception {

        String startDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(0));
        String endDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(1));
        Map<String, String> params = new HashMap<>();
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getR_SERVICE(), params, batchInfo); // 필요한 경우 batchInfo를 사용


        List<Map<String, Object>> services = serviceInfoManager.getServiceInfoList();

        if (rtn != null) {
            JsonNode agentSkillAssignments = rtn.path("skillSummaries");

            Date currentDate = new Date();
            long nineHoursInMillis = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
            Date koreaTime = new Date(currentDate.getTime() + nineHoursInMillis);
            Timestamp timestamp = new Timestamp(koreaTime.getTime());

            for (JsonNode item : agentSkillAssignments) {

                realTimeService service = new realTimeService();

                service.setId(item.get("skillId").asText());
                service.setEvent("service");
                service.setTimestamp(timestamp);
                service.setCompanyId(batchInfo.getCompanyId());
                service.setCampaignId(item.get("campaignId").asInt());
                service.setCampaignName(item.get("campaignName").asText());
                service.setSkillId(item.get("skillId").asInt());

                Map<String, Object> serviceInfo = services.stream()
                        .filter(serviceItem -> serviceItem.containsKey("ctiCode") &&
                                Integer.valueOf(serviceItem.get("ctiCode").toString()).equals(item.get("skillId").asInt()))
                        .findFirst()
                        .orElse(null); // 첫 번째 매칭된 값이 없으면 null 반환

                        if (serviceInfo != null) {
                            // label과 tenantId 값 추출
                            String label = (String) serviceInfo.get("label");
                            int tenantId = (int) serviceInfo.get("tenantId");
                            service.setName(label);
                            service.setTenantId(tenantId);
                        }else{
                            service.setName(item.get("skillName").asText());
                        }

                    // label이 존재하면 setName 호출
                    service.setContactsActive(item.get("contactsActive").asInt());
                    service.setIsOutbound(item.get("isOutbound").asBoolean());
                    service.setMediaTypeId(item.get("mediaTypeId").asInt());
                    service.setMediaTypeName(item.get("mediaTypeName").asText());
                    service.setOffer(item.get("contactsOffered").asInt());
                    service.setAns(item.get("contactsHandled").asInt());
                    service.setAbd(item.get("contactsOffered").asInt()-item.get("contactsHandled").asInt());
                    service.setWaiting(item.get("queueCount").asInt());
                    service.setDials(item.get("dials").asInt());
                    service.setAnsRate(item.get("contactsOffered").asInt() > 0 && item.get("contactsHandled").asInt() > 0 ?  item.get("contactsHandled").asInt() / item.get("contactsOffered").asInt() : 0);
                    service.setAbandonRate(item.get("abandonRate").asInt());
                    service.setConnects(item.get("connects").asInt());
                    service.setContactsQueued(item.get("contactsQueued").asInt());
                    service.setAgentsAcw(item.get("agentsAcw").asInt());
                    service.setAgentsAvailable(item.get("agentsAvailable").asInt());
                    service.setAgentsIdle(item.get("agentsIdle").asInt());
                    service.setAgentsLoggedIn(item.get("agentsLoggedIn").asInt());
                    service.setAgentsUnavailable(item.get("agentsUnavailable").asInt());
                    service.setAgentsWorking(item.get("agentsWorking").asInt());
                    service.setSvcLv(item.get("contactsOffered").asInt() == 0 ? 0 : item.get("serviceLevel").asInt());
                    service.setHoldTime(DateUtils.ConverToSecondFromPTS(item.get("contactsQueued").asText()));
                    service.setAverageHandleTime(DateUtils.ConverToSecondFromPTS(item.get("averageHandleTime").asText()));
                    service.setAverageInqueueTime(DateUtils.ConverToSecondFromPTS(item.get("averageInqueueTime").asText()));
                    service.setAverageSpeedToAnswer(DateUtils.ConverToSecondFromPTS(item.get("averageSpeedToAnswer").asText()));
                    service.setAvgtalktime(DateUtils.ConverToSecondFromPTS(item.get("averageTalkTime").asText()));
                    service.setTalktime(DateUtils.ConverToSecondFromPTS(item.get("averageTalkTime").asText())*item.get("contactsHandled").asInt());
                    service.setAverageWrapTime(DateUtils.ConverToSecondFromPTS(item.get("averageWrapTime").asText()));
                    service.setMaxWaitTime(DateUtils.ConverToSecondFromPTS(item.get("longestQueueDur").asText()));
                    service.setAvgWaitingTime(item.get("queueCount").asInt() == 0 ? 0 : DateUtils.ConverToSecondFromPTS(item.get("longestQueueDur").asText()) / item.get("queueCount").asInt());
                    service.setTotalContactTime(DateUtils.ConverToSecondFromPTS(item.get("totalContactTime").asText()));
                    service.setConnectsAHT(DateUtils.ConverToSecondFromPTS(item.get("connectsAHT").asText()));

                    mongoService.saveService(service);
            }
        }

    }

    @Override
    @Transactional
    public void getAgtServiceMap(BatchInfo batchInfo) throws Exception {

        Map<String, String> params = new HashMap<>();
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getECP_AGENT_SERVICE_MAP(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
            if (totalRecords > 0) {
                JsonNode agentSkillAssignments = rtn.path("agentSkillAssignments");
                List<DataMap> items = new ArrayList<>();
                for (JsonNode item : agentSkillAssignments) {
                    DataMap itemMap = new DataMap();

                    int agentId = item.get("agentId").asInt();
                    int skillId = item.get("skillId").asInt();
                    String skillName = item.get("skillName").asText();
                    int priority = item.get("agentProficiencyValue").asInt();

                    itemMap.put("agentId",agentId);
                    itemMap.put("skillId",skillId);
                    itemMap.put("skillName",skillName);
                    itemMap.put("priority",priority);
                    itemMap.put("companyId",batchInfo.getCompanyId());
                    itemMap.put("columnName","U_SERVICE.CTI_CODE");

                    items.add(itemMap);

                }

                DataMap contactList = new DataMap();
                contactList.put("list",items);
                CCaaSMapper.updateAgentServiceMap(contactList);
            }
        }
    }

    @Override
    @Transactional
    public void getRealTimeCampaign(BatchInfo batchInfo) throws Exception {


        String startDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(0));
        String endDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(1));
        String ymd = DateUtils.CurrentDateYMD(0);

        Map<String, String> params = new HashMap<>();
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getE_CONTACT(), params, batchInfo); // 필요한 경우 batchInfo를 사용

        if (rtn != null) {
            int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
            if( totalRecords > 0){
                JsonNode ContactNode = rtn.path("contacts");
                List<DataMap> items = new ArrayList<>();
                for (JsonNode item : ContactNode) {

                    if(item.get("pointOfContactId").asInt() != 10){
                        continue;
                    }

                    DataMap itemMap = new DataMap();

                    String contactStartDate = DateUtils.formatToTimestamp6Plus9Hours(item.get("contactStartDate").asText());

                    itemMap.put("companyId",batchInfo.getCompanyId());
                    itemMap.put("dateTime",contactStartDate);
                    itemMap.put("week",DateUtils.getDayOfWeek(ymd));
                    itemMap.put("holi","N");
                    itemMap.put("abandoned",item.get("abandoned").asBoolean() ? 1 : 0);
                    itemMap.put("abandonSeconds",item.get("abandonSeconds").asText());
                    itemMap.put("acwSeconds",item.get("acwSeconds").asText());
                    itemMap.put("agentId",item.get("agentId").asInt());
                    itemMap.put("agentSeconds",item.get("agentSeconds").asText());
                    itemMap.put("analyticsProcessedDate","");
                    itemMap.put("callbackTime",item.get("callbackTime").asText());
                    itemMap.put("campaignId",item.get("campaignId").asText());
                    itemMap.put("campaignName",item.get("campaignName").asText());
                    itemMap.put("conferenceSeconds",item.get("conferenceSeconds").asText());
                    itemMap.put("contactId",item.get("contactId").asText());
                    itemMap.put("contactStartDate",DateUtils.formatToTimestamp(item.get("contactStartDate").asText()));
                    itemMap.put("dateACWWarehoused",!item.hasNonNull("dateACWWarehoused") ? "" : DateUtils.formatToTimestamp(item.get("dateACWWarehoused").asText()));
                    itemMap.put("dateContactWarehoused",!item.hasNonNull("dateContactWarehoused")  ? "" :  DateUtils.formatToTimestamp(item.get("dateContactWarehoused").asText()));
                    itemMap.put("digitalContactStateId",item.get("digitalContactStateId").asInt());
                    itemMap.put("digitalContactStateName",item.get("digitalContactStateName").asText());
                    itemMap.put("dispositionNotes",item.get("dispositionNotes").asText());
                    itemMap.put("endReason",item.get("endReason").asText());
                    itemMap.put("firstName",item.get("firstName").asText());
                    itemMap.put("fromAddress",item.get("fromAddress").asText());
                    itemMap.put("highProficiency",item.get("highProficiency").asText());
                    itemMap.put("holdCount",item.get("holdCount").asText());
                    itemMap.put("holdSeconds",item.get("holdSeconds").asText());
                    itemMap.put("inQueueSeconds",item.get("inQueueSeconds").asText());
                    itemMap.put("isActive",item.get("isActive").asBoolean() ? 1 : 0);
                    itemMap.put("isAnalyticsProcessed",item.get("isAnalyticsProcessed").asBoolean() ? 1 : 0);
                    itemMap.put("isLogged",item.get("isLogged").asBoolean() ? 1 : 0);
                    itemMap.put("isOutbound",item.get("isOutbound").asBoolean() ? 1 : 0);
                    itemMap.put("isRefused",item.get("isRefused").asBoolean() ? 1 : 0);
                    itemMap.put("isShortAbandon",item.get("isShortAbandon").asBoolean() ? 1 : 0);
                    itemMap.put("isTakeover",item.get("isTakeover").asBoolean() ? 1 : 0);
                    itemMap.put("isWarehoused",item.get("isWarehoused").asBoolean() ? 1 : 0);
                    itemMap.put("lastName",item.get("lastName").asText());
                    itemMap.put("lastUpdateTime",DateUtils.formatToTimestamp(item.get("lastUpdateTime").asText()));
                    itemMap.put("lowProficiency",item.get("lowProficiency").asText());
                    itemMap.put("masterContactId",item.get("masterContactId").asText());
                    itemMap.put("mediaSubTypeId",item.get("mediaSubTypeId").asInt());
                    itemMap.put("mediaSubTypeName",item.get("mediaSubTypeName").asText());
                    itemMap.put("mediaTypeId",item.get("mediaTypeId").asText());
                    itemMap.put("mediaTypeName",item.get("mediaTypeName").asText());
                    itemMap.put("pointOfContactId",item.get("pointOfContactId").asInt());
                    itemMap.put("pointOfContactName",item.get("pointOfContactName").asText());
                    itemMap.put("postQueueSeconds",item.get("postQueueSeconds").asText());
                    itemMap.put("preQueueSeconds",item.get("preQueueSeconds").asText());
                    itemMap.put("primaryDispositionId",item.get("primaryDispositionId").asInt());
                    itemMap.put("refuseReason",item.get("refuseReason").asText());
                    itemMap.put("refuseTime",item.get("refuseTime").asText());
                    itemMap.put("releaseSeconds",item.get("releaseSeconds").asText());
                    itemMap.put("routingAttribute",item.get("routingAttribute").asText());
                    itemMap.put("routingTime",item.get("routingTime").asInt() != 0 ? item.get("routingTime").asInt()/1000 : 0);
                    itemMap.put("secondaryDispositionId",item.get("secondaryDispositionId").asInt());
                    itemMap.put("serviceLevelFlag",item.get("serviceLevelFlag").asText());
                    itemMap.put("skillId",item.get("skillId").asInt());
                    itemMap.put("skillName",item.get("skillName").asText());
                    itemMap.put("stateId",item.get("stateId").asInt());
                    itemMap.put("stateName",item.get("stateName").asText());
                    itemMap.put("teamId",item.get("teamId").asInt());
                    itemMap.put("teamName",item.get("teamName").asText());
                    itemMap.put("toAddress",item.get("toAddress").asText());
                    itemMap.put("totalDurationSeconds",item.get("totalDurationSeconds").asText());
                    itemMap.put("transferIndicatorId",item.get("transferIndicatorId").asInt());
                    itemMap.put("transferIndicatorName",item.get("transferIndicatorName").asText());

                    items.add(itemMap);
                }
                DataMap contactList = new DataMap();
                contactList.put("list",items);
                CCaaSMapper.insertEcontactPds(contactList);
            }
        } else {
            log.info("getU_Poc service API response is null.");
        }
    }
}