package com.ecstel.sym.service.nice;

import com.ecstel.sym.base.define;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.utils.DateUtils;
import com.ecstel.sym.vo.BatchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecstel.sym.service.EvtService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Component("niceDailyBatchService")
@RequiredArgsConstructor
public class DailyBatchService implements com.ecstel.sym.service.DailyBatchService {

    @Autowired
    private com.ecstel.sym.config.BatchListConfig BatchListConfig;

    @Autowired
    private EvtService evtService;

    @Autowired
    private StatService statService;

    private final CCaaSDbMapper CCaaSMapper;
    @Override
    @Transactional
    public void getDailyBatch(BatchInfo batchInfo,String ymd) throws Exception {

        int days = -1;
        if(ymd != null){
            days = (int) DateUtils.calculateDaysDifference(ymd);
        }
        System.out.println("days>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. days " +days);


        if (BatchListConfig.getE_CONTACT()) {
            DataMap paramMap = createBatchCheckParams(define.E_CONTACT,batchInfo,days);

            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));

            if (needsProcessing) {
                boolean isevt = evtService.getEContact(batchInfo, days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getC_INBOUND()) {
            DataMap paramMap = createBatchCheckParams(define.C_INBOUND,batchInfo,days);

            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));

            if (needsProcessing) {
                boolean isevt = evtService.getCInbound(batchInfo, days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getC_OUTBOUND()) {
            DataMap paramMap = createBatchCheckParams(define.C_OUTBOUND,batchInfo,days);

            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));

            if (needsProcessing) {
                boolean isevt = evtService.getCOutbound(batchInfo, days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getC_CAMPAIGN()) {
            DataMap paramMap = createBatchCheckParams(define.C_CAMPAIGN,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = evtService.getCCampaign(batchInfo, days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }


        if (BatchListConfig.getE_AGENT_STATUS()) {
            DataMap paramMap = createBatchCheckParams(define.E_AGT_STATE,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = statService.getEAgentStatus(batchInfo,days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getE_AGENT_STATUS()) {
            DataMap paramMap = createBatchCheckParams(define.STAT_H_AGT_STATE,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = statService.getStatHAgentStatus(batchInfo,days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getE_AGENT_STATUS()) {
            DataMap paramMap = createBatchCheckParams(define.STAT_H_AGT_INBOUND,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = statService.getStatHAgentInbound(batchInfo,days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getE_AGENT_STATUS()) {
            DataMap paramMap = createBatchCheckParams(define.STAT_H_AGT_OUTBOUND,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = statService.getStatHAgentOutbound(batchInfo,days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getE_AGENT_STATUS()) {
            DataMap paramMap = createBatchCheckParams(define.STAT_H_AGT,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = statService.getStatHAgent(batchInfo,days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }

        if (BatchListConfig.getSTAT_H_SERVICE()) {
            DataMap paramMap = createBatchCheckParams(define.STAT_H_SERVICE,batchInfo,days);
            List<Map<String, Object>> batchStatus = CCaaSMapper.selectBatchCheck(paramMap);
            boolean needsProcessing = batchStatus.isEmpty() || "X".equals(batchStatus.get(0).get("status"));
            if (needsProcessing) {
                boolean isevt = statService.getStatHService(batchInfo,days);
                setBatchCheck(paramMap, batchInfo.getCompanyId(), isevt);
            }
        }





    }

    private String formatCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return dateFormat.format(new Date());
    }

    private DataMap createBatchCheckParams(String batchName,BatchInfo batchInfo,int days) {
        DataMap paramMap = new DataMap();
        paramMap.put("batchName", batchName);
        paramMap.put("ymd", DateUtils.CurrentDateYMD(days));
        return paramMap;
    }

    private void setBatchCheck(DataMap paramMap, int companyId, boolean isevt) {
        paramMap.put("companyId", companyId);
        paramMap.put("batchStartTime", formatCurrentTime());
        paramMap.put("batchEndTime", formatCurrentTime());
        paramMap.put("status", isevt ? "O" : "X");

        CCaaSMapper.setBatchCheck(paramMap);
    }
}
