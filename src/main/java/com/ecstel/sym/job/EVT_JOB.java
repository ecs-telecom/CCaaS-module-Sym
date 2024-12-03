package com.ecstel.sym.job;

import com.ecstel.sym.config.BatchListConfig;
import com.ecstel.sym.service.EvtService;
import com.ecstel.sym.service.InfoService;
import com.ecstel.sym.vo.BatchInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class EVT_JOB extends QuartzJobBean implements InterruptableJob {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private com.ecstel.sym.config.BatchListConfig BatchListConfig;

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// Optional: 잡을 중지해야 할 경우 구현
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();

		BatchInfo batchInfo = (BatchInfo) jobDataMap.get("batchInfo");

		CompletableFuture<Integer> E_CONTACT = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getE_CONTACT()){
					E_CONTACT(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> C_INBOUND = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getC_INBOUND()){
					C_INBOUND(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> C_OUTBOUND = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getC_OUTBOUND()){
					C_OUTBOUND(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> C_CAMPAIGN = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getC_CAMPAIGN()){
					C_CAMPAIGN(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> setComponent = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getC_CAMPAIGN()){
					setComponent(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});




		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(E_CONTACT,C_INBOUND,C_OUTBOUND,C_CAMPAIGN);

		combinedFuture.handle((result, throwable) -> {
			if (throwable != null) {
				log.error("Error occurred during batch processing: {}", throwable.getMessage());
			} else {
				try {
					Integer ECONTACTResult = E_CONTACT.get();
					Integer CINBOUNDResult = C_INBOUND.get();
					Integer C_OUTBOUNDResult = C_OUTBOUND.get();
					Integer C_CAMPAIGNResult = C_CAMPAIGN.get();
					Integer setComponentResult = setComponent.get();

					log.info("Batch processing completed successfully. Results: E_CONTACT={}, C_INBOUND={},  C_OUTBOUND={}, C_CAMPAIGNResult={} setComponentResult={}", ECONTACTResult, CINBOUNDResult, C_OUTBOUNDResult,C_CAMPAIGNResult,setComponentResult);
				} catch (InterruptedException | ExecutionException e) {
					log.error("Error retrieving results: {}", e.getMessage());
				}
			}
			return null; // handle 메소드는 T 결과를 반환하므로 null을 반환
		});
	}

	private void E_CONTACT(BatchInfo batchInfo) throws Exception {


		EvtService evtService = getInfoService(batchInfo);
		if (evtService != null) {
			evtService.getEContact(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void C_INBOUND(BatchInfo batchInfo) throws Exception {


		EvtService evtService = getInfoService(batchInfo);
		if (evtService != null) {
			evtService.getCInbound(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void C_OUTBOUND(BatchInfo batchInfo) throws Exception {


		EvtService evtService = getInfoService(batchInfo);
		if (evtService != null) {
			evtService.getCOutbound(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void C_CAMPAIGN(BatchInfo batchInfo) throws Exception {


		EvtService evtService = getInfoService(batchInfo);
		if (evtService != null) {
			evtService.getCCampaign(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void setComponent(BatchInfo batchInfo) throws Exception {


		EvtService evtService = getInfoService(batchInfo);
		if (evtService != null) {
			evtService.setComponentInfo(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}




	private EvtService getInfoService(BatchInfo batchInfo) {
		switch (batchInfo.getContactCenterType()) {
			case "NICE":
				return applicationContext.getBean("niceEvtService", EvtService.class);
			case "BRIGHT_PATTERN":
				return applicationContext.getBean("bpEvtService", EvtService.class);
			default:
				log.warn("Unsupported Contact Center Type: {}", batchInfo.getContactCenterType());
				return null;
		}
	}
}