package com.ecstel.sym.service.nice;

import com.ecstel.sym.config.NiceApiUrlConfig;
import com.ecstel.sym.httpClient.ApiService;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.mapper.ecp.EcpDbMapper;
import com.ecstel.sym.service.ServiceInfoManager;
import com.ecstel.sym.service.base.BaseEvtService;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.utils.DateUtils;
import com.ecstel.sym.utils.StringUtil;
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
import com.ecstel.sym.base.define;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component("niceEvtService")
@RequiredArgsConstructor
public class EvtService extends BaseEvtService implements com.ecstel.sym.service.EvtService {

    private static final Logger log = LoggerFactory.getLogger(EvtService.class);

    @Autowired
    private ApiService apiService; // ApiService 주입

    @Autowired
    private NiceApiUrlConfig NiceApiUrlConfig; // ApiService 주입

    @Autowired
    private ServiceInfoManager serviceInfoManager;

    private final CCaaSDbMapper CCaaSMapper;
    private final EcpDbMapper EcpMapper;

    @Override
    @Transactional
    public boolean getEContact(BatchInfo batchInfo,int days)  throws Exception {
        try{
            boolean dailyBatch =false;

            if(days != 0){
                dailyBatch  = true;
            }

            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.E_CONTACT);  //매일 01일에는 파티션을 추가해준다.
            }

            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());
            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectEcontactMaxData(paramMap);


            String startDate = "";
            String endDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(days+1));

            if(dailyBatch){
                startDate = DateUtils.convertUTCDate(ymd);
                DataMap paramMap2 = new DataMap();
                paramMap2.put("lastDateTime",DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd)));
                paramMap2.put("companyId",batchInfo.getCompanyId());
                CCaaSMapper.deleteEcontactData(paramMap2);
            }else{
                if(maxDateTime.get(0) == null){
                    startDate = DateUtils.convertUTCDate(ymd);
                }else{
                    DataMap paramMap2 = new DataMap();
                    paramMap2.put("lastDateTime",maxDateTime.get(0).get("lastDatetime").toString());
                    paramMap2.put("companyId",batchInfo.getCompanyId());
                    CCaaSMapper.deleteEcontactData(paramMap2);
                    startDate = maxDateTime.get(0).get("formattedDatetime").toString();
                }
            }

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
                    CCaaSMapper.insertEcontact(contactList);
                }
            } else {
                log.info("getU_Poc service API response is null.");
            }

            return true;
        }catch(Exception E){

            return false;
        }
    }

    @Override
    @Transactional
    public boolean getCInbound(BatchInfo batchInfo,int days) throws Exception {
        try{
            boolean dailyBatch =false;

            if(days != 0){
                dailyBatch  = true;
            }

            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.C_INBOUND);  //매일 01일에는 파티션을 추가해준다.
            }


            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());
            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectCInboundMaxData(paramMap);

            String lastDateTime = "";

            if(dailyBatch) {
                lastDateTime = DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd));
                DataMap paramMap2 = new DataMap();
                paramMap2.put("lastDateTime",lastDateTime);
                paramMap2.put("companyId",batchInfo.getCompanyId());
                paramMap2.put("ymd",ymd);
                CCaaSMapper.deleteCInboundData(paramMap2);
            }else{
                if(maxDateTime.get(0) == null){
                    lastDateTime = DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd));
                }else{
                    lastDateTime = maxDateTime.get(0).get("lastDatetime").toString();
                    DataMap paramMap2 = new DataMap();
                    paramMap2.put("lastDateTime",lastDateTime);
                    paramMap2.put("companyId",batchInfo.getCompanyId());
                    paramMap2.put("ymd",ymd);
                    CCaaSMapper.deleteCInboundData(paramMap2);
                }
            }



            DataMap insertCinboundData = new DataMap();
            insertCinboundData.put("companyId",batchInfo.getCompanyId());
            insertCinboundData.put("ymd",ymd);
            insertCinboundData.put("lastDateTime",lastDateTime);

            String date = DateUtils.convertUTCDate(ymd);

            CCaaSMapper.insertCInbound(insertCinboundData);

            return true;
        }catch(Exception e){
            return false;
        }

    }

    @Override
    @Transactional
    public boolean getCOutbound(BatchInfo batchInfo,int days) throws Exception {
        try{

            boolean dailyBatch =false;

            if(days != 0){
                dailyBatch  = true;
            }


            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.C_OUTBOUND);  //매일 01일에는 파티션을 추가해준다.
            }


            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());
            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectCOutboundMaxData(paramMap);


            String lastDateTime = "";

            if(dailyBatch) {
                lastDateTime = DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd));
                DataMap paramMap2 = new DataMap();
                paramMap2.put("lastDateTime",lastDateTime);
                paramMap2.put("companyId",batchInfo.getCompanyId());
                paramMap2.put("ymd",ymd);
                CCaaSMapper.deleteCOutboundData(paramMap2);
            }else{
                if(maxDateTime.get(0) == null){
                    lastDateTime = DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd));
                }else{
                    lastDateTime = maxDateTime.get(0).get("lastDatetime").toString();
                    DataMap paramMap2 = new DataMap();
                    paramMap2.put("lastDateTime",lastDateTime);
                    paramMap2.put("companyId",batchInfo.getCompanyId());
                    paramMap2.put("ymd",ymd);
                    CCaaSMapper.deleteCOutboundData(paramMap2);
                }
            }


            DataMap insertCinboundData = new DataMap();
            insertCinboundData.put("companyId",batchInfo.getCompanyId());
            insertCinboundData.put("ymd",ymd);
            insertCinboundData.put("lastDateTime",lastDateTime);

            String date = DateUtils.convertUTCDate(ymd);

            CCaaSMapper.insertCOutbound(insertCinboundData);

            return true;
        }catch(Exception E){
            E.printStackTrace();
            return false;
        }

    }

    @Override
    @Transactional
    public boolean getCCampaign(BatchInfo batchInfo,int days) throws Exception {
        try{
            boolean dailyBatch =false;
            if(days != 0){
                dailyBatch  = true;
            }
            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.C_CAMPAIGN);  //매일 01일에는 파티션을 추가해준다.
            }


            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());
            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectCCampaignMaxData(paramMap);


            String lastDateTime = "";
            if(dailyBatch){
                lastDateTime = DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd));
                DataMap paramMap2 = new DataMap();
                paramMap2.put("lastDateTime",lastDateTime);
                paramMap2.put("companyId",batchInfo.getCompanyId());
                paramMap2.put("ymd",ymd);
                CCaaSMapper.deleteCCampaignData(paramMap2);
            }else{
                if(maxDateTime.get(0) == null){
                    lastDateTime = DateUtils.formatToTimestamp6Plus9Hours(DateUtils.convertUTCDate(ymd));
                }else{
                    lastDateTime = maxDateTime.get(0).get("lastDatetime").toString();
                    DataMap paramMap2 = new DataMap();
                    paramMap2.put("lastDateTime",lastDateTime);
                    paramMap2.put("companyId",batchInfo.getCompanyId());
                    paramMap2.put("ymd",ymd);
                    CCaaSMapper.deleteCCampaignData(paramMap2);
                }
            }
            DataMap insertCinboundData = new DataMap();
            insertCinboundData.put("companyId",batchInfo.getCompanyId());
            insertCinboundData.put("ymd",ymd);
            insertCinboundData.put("lastDateTime",lastDateTime);
            String date = DateUtils.convertUTCDate(ymd);
            CCaaSMapper.insertCCampaign(insertCinboundData);

            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    @Transactional
    public boolean setComponentInfo(BatchInfo batchInfo,int days) throws Exception {
        try{

            DataMap paramMap = new DataMap();
            paramMap.put("companyId",batchInfo.getCompanyId());
            List<Map<String, Object>> CCaaSServiceInfo =  CCaaSMapper.setlectUserviceData(paramMap);

            serviceInfoManager.clearServiceInfoList();
            for (Map<String, Object> serviceInfo : CCaaSServiceInfo) {
                serviceInfoManager.addServiceInfo(serviceInfo);
            }

            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
