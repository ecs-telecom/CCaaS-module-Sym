package com.ecstel.sym.service.nice;

import com.ecstel.sym.base.define;
import com.ecstel.sym.config.BatchStatConfig;
import com.ecstel.sym.config.NiceApiUrlConfig;
import com.ecstel.sym.httpClient.ApiService;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.mapper.ecp.EcpDbMapper;
import com.ecstel.sym.service.base.BaseEvtService;
import com.ecstel.sym.service.base.BaseStatService;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.utils.DateUtils;
import com.ecstel.sym.utils.StringUtil;
import com.ecstel.sym.vo.BatchInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component("niceStatService")
@RequiredArgsConstructor
public class StatService extends BaseStatService implements com.ecstel.sym.service.StatService {

    private static final Logger log = LoggerFactory.getLogger(StatService.class);

    @Autowired
    private ApiService apiService; // ApiService 주입

    @Autowired
    private NiceApiUrlConfig NiceApiUrlConfig; // ApiService 주입

    private final CCaaSDbMapper CCaaSMapper;
    private final EcpDbMapper EcpMapper;

    @Autowired
    private BatchStatConfig batchStatConfig;

    @Override
    @Transactional
    public boolean getEAgentStatus(BatchInfo batchInfo,int days) throws Exception {

        boolean dailyBatch =false;

        if(days != 0){
            dailyBatch  = true;
        }

        try{

            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.E_AGT_STATE);  //매일 01일에는 파티션을 추가해준다.
            }

            String startDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(days));
            String endDate = DateUtils.convertUTCDate(DateUtils.CurrentDateYMD(days+1));

            DataMap paramMap2 = new DataMap();
            paramMap2.put("ymd",ymd);
            paramMap2.put("companyId",batchInfo.getCompanyId());
            CCaaSMapper.deleteEAgentState(paramMap2);


            Map<String, String> params = new HashMap<>();
            params.put("startDate",startDate);
            params.put("endDate",endDate);

            JsonNode rtn = apiService.makeApiCall(NiceApiUrlConfig.getE_AGENT_STATUS(), params, batchInfo); // 필요한 경우 batchInfo를 사용


            if (rtn != null) {
                List<DataMap> items = new ArrayList<>();
                int totalRecords = Integer.parseInt(rtn.get("totalRecords").asText());
                if( totalRecords > 0){
                    JsonNode ContactNode = rtn.path("agentStateHistory");

                    for (JsonNode item : ContactNode) {

                        String startDateTime = DateUtils.formatToTimestamp6Plus9Hours(item.get("startDate").asText());
                        int agentId = item.get("agentId").asInt();
                        int duration = DateUtils.ConverToSecondFromPTS(item.get("duration").asText());
                        LocalDateTime IntervalDateTime = DateUtils.formatToTimestampInterval(startDateTime);
                        LocalDateTime IntervalDateTimePlus15 = IntervalDateTime.plusMinutes(15);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
                        LocalDateTime startDateTime2 = LocalDateTime.parse(startDateTime, formatter);
                        long diffInMilliseconds = Duration.between(startDateTime2, IntervalDateTimePlus15).getSeconds();
                        int extraInserts = diffInMilliseconds == 0 ? 0 : (int) (duration / diffInMilliseconds);

                        if(diffInMilliseconds < duration) {
                            List<Integer> list = DateUtils.calculateTimeInIntervals(startDateTime, IntervalDateTimePlus15, duration, extraInserts);

                            for (int i = 0; i < list.size(); i++) {

                                LocalDateTime IntervalDate = DateUtils.formatToTimestampInterval(startDateTime);
                                IntervalDate = IntervalDate.plusMinutes(i * 15);
                                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

                                DataMap itemMap = new DataMap();
                                itemMap.put("companyId", batchInfo.getCompanyId());
                                itemMap.put("startDateTime", startDateTime);
                                itemMap.put("currentStartDateTime", IntervalDate.format(outputFormatter));
                                itemMap.put("agentId", StringUtil.getStringValue(Integer.toString(agentId)));
                                itemMap.put("stateIndex", item.get("stateIndex").asInt());
                                itemMap.put("agentStateId", item.get("agentStateId").asInt());
                                itemMap.put("agentStateName", StringUtil.getStringValue(item.get("agentStateName").asText()));
                                itemMap.put("agentSessionId", item.get("agentSessionId").asInt());
                                itemMap.put("contactId", item.get("contactId").asInt());
                                itemMap.put("skillId", item.get("skillId").asInt());
                                itemMap.put("skillName", StringUtil.getStringValue(item.get("skillName").asText()));
                                itemMap.put("mediaTypeId", item.get("mediaTypeId").asInt());
                                itemMap.put("mediaTypeName", StringUtil.getStringValue(item.get("mediaTypeName").asText()));
                                itemMap.put("fromAddress", StringUtil.getStringValue(item.get("fromAddress").asText()));
                                itemMap.put("toAddress", StringUtil.getStringValue(item.get("toAddress").asText()));
                                itemMap.put("outStateId", item.get("outStateId").asInt());
                                itemMap.put("outStateDescription", StringUtil.getStringValue(item.get("outStateDescription").asText()));
                                itemMap.put("duration", list.get(i));

                                itemMap.put("week",DateUtils.getDayOfWeek(ymd));
                                items.add(itemMap);
                            }
                        }else{
                            LocalDateTime IntervalDate = DateUtils.formatToTimestampInterval(startDateTime);
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

                            DataMap itemMap = new DataMap();
                            itemMap.put("companyId",batchInfo.getCompanyId());
                            itemMap.put("startDateTime",startDateTime);
                            itemMap.put("currentStartDateTime",IntervalDate.format(outputFormatter));
                            itemMap.put("agentId",Integer.toString(agentId));
                            itemMap.put("stateIndex",item.get("stateIndex").asInt());
                            itemMap.put("agentStateId",item.get("agentStateId").asInt());
                            itemMap.put("agentStateName", StringUtil.getStringValue(item.get("agentStateName").asText()));
                            itemMap.put("agentSessionId",item.get("agentSessionId").asInt());
                            itemMap.put("contactId",item.get("contactId").asInt());
                            itemMap.put("skillId",item.get("skillId").asInt());
                            itemMap.put("skillName",StringUtil.getStringValue(item.get("skillName").asText()));
                            itemMap.put("mediaTypeId",item.get("mediaTypeId").asInt());
                            itemMap.put("mediaTypeName",StringUtil.getStringValue(item.get("mediaTypeName").asText()));
                            itemMap.put("fromAddress",StringUtil.getStringValue(item.get("fromAddress").asText()));
                            itemMap.put("toAddress",StringUtil.getStringValue(item.get("toAddress").asText()));
                            itemMap.put("outStateId",item.get("outStateId").asInt());
                            itemMap.put("outStateDescription", StringUtil.getStringValue(item.get("outStateDescription").asText()));
                            itemMap.put("duration",duration);

                            itemMap.put("week",DateUtils.getDayOfWeek(ymd));
                            items.add(itemMap);
                        }
                    }

                    DataMap contactList = new DataMap();
                    contactList.put("list",items);
                    CCaaSMapper.insertEagentState(contactList);
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }

    }


    @Override
    @Transactional
    public boolean getStatHAgentStatus(BatchInfo batchInfo,int days) throws Exception {
        boolean dailyBatch =false;

        if(days != 0){
            dailyBatch  = true;
        }


        try{
            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.STAT_H_AGT_STATE);  //매일 01일에는 파티션을 추가해준다.
            }

            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());

            if(dailyBatch){
                CCaaSMapper.deleteStatHAgentStateData(paramMap);
            }

            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectStatHAgentStateMaxData(paramMap);

            String startDate = "";

            if(maxDateTime.get(0) == null){
                startDate = String.valueOf(DateUtils.convertDateYMD(ymd)) + " 00:00:00.000";
            }else{
                startDate = maxDateTime.get(0).get("lastDatetime").toString();
            }


            DataMap params = new DataMap();
            params.put("lastDateTime",startDate);
            params.put("ymd",ymd);
            params.put("companyId",batchInfo.getCompanyId());

            if(batchStatConfig.getIflag()){
                CCaaSMapper.insertStatHAgentStateI(params);
            }
            return true;
        }catch(Exception e){
            return false;
        }
    }





    @Override
    @Transactional
    public boolean getStatHService(BatchInfo batchInfo,int days) throws Exception {
        boolean dailyBatch =false;

        if(days != 0){
            dailyBatch  = true;
        }


        try{
            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.STAT_H_SERVICE);  //매일 01일에는 파티션을 추가해준다.
            }
            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());

            if(dailyBatch){
                CCaaSMapper.deleteStatHserviceData(paramMap);
            }

            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectStatHServiceMaxData(paramMap);

            String startDate = "";

            if(maxDateTime.get(0) == null){
                startDate = String.valueOf(DateUtils.convertDateYMD(ymd)) + " 00:00:00.000";
            }else{
                startDate = maxDateTime.get(0).get("lastDatetime").toString();
            }


            DataMap params = new DataMap();
            params.put("lastDateTime",startDate);
            params.put("ymd",ymd);
            params.put("companyId",batchInfo.getCompanyId());

            if(batchStatConfig.getIflag()){
                CCaaSMapper.insertStatHServiceI(params);
            }

            if(batchStatConfig.getI30flag()){
                params.put("targetTable","stat_h_ibg_i30");
                params.put("sourceTable","stat_h_ibg_i");
                params.put("timestamp","STR_TO_DATE(CONCAT(DATE_FORMAT(timestamp, '%Y%m%d%H'),\n" +
                        "        CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(timestamp, '%H%i'), 3, 2)/30) = 0 THEN '00' ELSE '30' END\n" +
                        "        ), '%Y%m%d%H%i') ");
                params.put("hh24mi","CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(Timestamp, '%H%i'), 3, 2)/30) = 0 THEN concat(HH24,'00') ELSE concat(HH24,'30') END");
                CCaaSMapper.insertStatHServiceSummary(params);
            }

            if(batchStatConfig.getHflag()){
                params.put("targetTable","STAT_H_IBG_H");
                params.put("sourceTable","STAT_H_IBG_I30");
                params.put("timestamp","STR_TO_DATE(CONCAT(DATE_FORMAT(timestamp, '%Y%m%d%H'),\n" +
                        "        CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(timestamp, '%H%i'), 3, 2)/60) = 0 THEN '00' ELSE '00' END\n" +
                        "        ), '%Y%m%d%H%i') ");
                params.put("hh24mi","CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(Timestamp, '%H%i'), 3, 2)/60) = 0 THEN concat(HH24,'00') ELSE concat(HH24,'00') END");
                CCaaSMapper.insertStatHServiceSummary(params);
            }

            if(batchStatConfig.getDflag()){
                params.put("targetTable","STAT_H_IBG_D");
                params.put("sourceTable","STAT_H_IBG_H");
                params.put("timestamp","DATE_FORMAT(TIMESTAMP, '%Y-%m-%d 00:00:00.000')");
                params.put("hh24mi","0000");
                CCaaSMapper.insertStatHServiceSummary(params);
            }
            return true;
        }catch(Exception e){
            return false;
        }
    }


    @Override
    @Transactional
    public boolean getStatHAgent(BatchInfo batchInfo,int days) throws Exception {

        boolean dailyBatch =false;

        if(days != 0){
            dailyBatch  = true;
        }


        try{
            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.STAT_H_AGT);  //매일 01일에는 파티션을 추가해준다.
            }

            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());

            if(dailyBatch){
                CCaaSMapper.deleteStatHAgtData(paramMap);
            }


            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectStatHAgtMaxData(paramMap);

            String startDate = "";

            if(maxDateTime.get(0) == null){
                startDate = String.valueOf(DateUtils.convertDateYMD(ymd)) + " 00:00:00.000";
            }else{
                startDate = maxDateTime.get(0).get("lastDatetime").toString();
            }


            DataMap params = new DataMap();
            params.put("lastDateTime",startDate);
            params.put("ymd",ymd);
            params.put("companyId",batchInfo.getCompanyId());

            if(batchStatConfig.getIflag()){
                CCaaSMapper.insertStatHAgtI(params);

                List<Map<String, Object>> agentInboundData =  CCaaSMapper.selectHagentInboundData(params);
                List<DataMap> items = new ArrayList<>();
                for (Map<String, Object> data : agentInboundData) {
                    DataMap itemMap = new DataMap();
                    itemMap.put("timestamp",data.get("timestamp"));
                    itemMap.put("companyId",data.get("companyId"));
                    itemMap.put("tenantId",data.get("tenantId"));
                    itemMap.put("centerId",data.get("centerId"));
                    itemMap.put("teamId",data.get("teamId"));
                    itemMap.put("partId",data.get("partId"));
                    itemMap.put("agtid",data.get("agtid"));
                    itemMap.put("offer",data.get("offer"));
                    itemMap.put("ans",data.get("ans"));
                    itemMap.put("abd",data.get("abd"));
                    itemMap.put("talktime",data.get("talktime"));
                    itemMap.put("acwtime",data.get("acwtime"));
                    itemMap.put("ringtime",data.get("ringtime"));
                    items.add(itemMap);
                }
                DataMap InboundList = new DataMap();
                InboundList.put("list",items);

                if(agentInboundData.size() > 0){
                    CCaaSMapper.insertStatHAgtInboundData(InboundList);
                }

                List<Map<String, Object>> agentOutboundData =  CCaaSMapper.selectHagentOutboundData(params);
                List<DataMap> outItems = new ArrayList<>();
                for (Map<String, Object> data : agentOutboundData) {
                    DataMap outItemMap = new DataMap();
                    outItemMap.put("timestamp",data.get("timestamp"));
                    outItemMap.put("companyId",data.get("companyId"));
                    outItemMap.put("tenantId",data.get("tenantId"));
                    outItemMap.put("centerId",data.get("centerId"));
                    outItemMap.put("teamId",data.get("teamId"));
                    outItemMap.put("partId",data.get("partId"));
                    outItemMap.put("agtid",data.get("agtid"));
                    outItemMap.put("obtry",data.get("obtry"));
                    outItemMap.put("ans",data.get("ans"));
                    outItemMap.put("abd",data.get("abd"));
                    outItemMap.put("talktime",data.get("talktime"));
                    outItemMap.put("holdtime",data.get("holdtime"));
                    outItems.add(outItemMap);
                }
                DataMap outboundList = new DataMap();
                outboundList.put("list",outItems);

                if(agentOutboundData.size() > 0){
                    CCaaSMapper.insertStatHAgtOutboundData(outboundList);
                }

            }

            if(batchStatConfig.getI30flag()){
                params.put("targetTable","STAT_H_AGT_I30");
                params.put("sourceTable","STAT_H_AGT_I");
                params.put("timestamp","STR_TO_DATE(CONCAT(DATE_FORMAT(timestamp, '%Y%m%d%H'),\n" +
                        "        CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(timestamp, '%H%i'), 3, 2)/30) = 0 THEN '00' ELSE '30' END\n" +
                        "        ), '%Y%m%d%H%i')  ");
                params.put("hh24mi","CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(Timestamp, '%H%i'), 3, 2)/30) = 0 THEN concat(HH24,'00') ELSE concat(HH24,'30') END");
                CCaaSMapper.insertStatHAgentSummary(params);
            }

            if(batchStatConfig.getHflag()){
                params.put("targetTable","STAT_H_AGT_H");
                params.put("sourceTable","STAT_H_AGT_I30");
                params.put("timestamp","STR_TO_DATE(CONCAT(DATE_FORMAT(timestamp, '%Y%m%d%H'),\n" +
                        "        CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(timestamp, '%H%i'), 3, 2)/60) = 0 THEN '00' ELSE '00' END\n" +
                        "        ), '%Y%m%d%H%i') ");
                params.put("hh24mi","CASE WHEN FLOOR(SUBSTRING(TIME_FORMAT(Timestamp, '%H%i'), 3, 2)/60) = 0 THEN concat(HH24,'00') ELSE concat(HH24,'00') END");
                CCaaSMapper.insertStatHAgentSummary(params);
            }

            if(batchStatConfig.getDflag()){
                params.put("targetTable","STAT_H_AGT_D");
                params.put("sourceTable","STAT_H_AGT_H");
                params.put("timestamp","DATE_FORMAT(TIMESTAMP, '%Y-%m-%d 00:00:00.000')");
                params.put("hh24mi","0000");
                CCaaSMapper.insertStatHAgentSummary(params);
            }
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Override
    @Transactional
    public boolean getStatHAgentInbound(BatchInfo batchInfo, int days) throws Exception {

        boolean dailyBatch =false;

        if(days != 0){
            dailyBatch  = true;
        }

        try{
            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.STAT_H_AGT_INBOUND);  //매일 01일에는 파티션을 추가해준다.
            }


            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());

            if(dailyBatch){
                CCaaSMapper.deleteStatHAgtinboundData(paramMap);
            }

            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectStatHAgtinboundMaxData(paramMap);

            String startDate = "";



            if(maxDateTime.get(0) == null){
                startDate = String.valueOf(DateUtils.convertDateYMD(ymd)) + " 00:00:00.000";
            }else{
                startDate = maxDateTime.get(0).get("lastDatetime").toString();
            }


            DataMap params = new DataMap();
            params.put("lastDateTime",startDate);
            params.put("ymd",ymd);
            params.put("companyId",batchInfo.getCompanyId());

            if(batchStatConfig.getIflag()){
                CCaaSMapper.insertStatHAgtInbound(params);
            }
            return true;
        }catch(Exception e){
            return false;
        }

    }

    @Override
    @Transactional
    public boolean getStatHAgentOutbound(BatchInfo batchInfo,int days) throws Exception {

        boolean dailyBatch =false;

        if(days != 0){
            dailyBatch  = true;
        }

        try{
            String ymd = DateUtils.CurrentDateYMD(days);
            String dd = ymd.substring(6, 8);
            if(dd.equals("01")){
                ExistsPartition(ymd,define.STAT_H_AGT_INBOUND);  //매일 01일에는 파티션을 추가해준다.
            }

            DataMap paramMap = new DataMap();
            paramMap.put("ymd",ymd);
            paramMap.put("companyId",batchInfo.getCompanyId());

            if(dailyBatch){
                CCaaSMapper.deleteStatHAgtoutboundData(paramMap);
            }

            List<Map<String, Object>> maxDateTime =  CCaaSMapper.selectStatHAgtOutboundMaxData(paramMap);

            String startDate = "";

            if(maxDateTime.get(0) == null){
                startDate = String.valueOf(DateUtils.convertDateYMD(ymd)) + " 00:00:00.000";
            }else{
                startDate = maxDateTime.get(0).get("lastDatetime").toString();
            }


            DataMap params = new DataMap();
            params.put("lastDateTime",startDate);
            params.put("ymd",ymd);
            params.put("companyId",batchInfo.getCompanyId());

            if(batchStatConfig.getIflag()){
                CCaaSMapper.insertStatHAgtOutbound(params);
            }

            return true;
        }catch(Exception e){
            return false;
        }

    }
}
