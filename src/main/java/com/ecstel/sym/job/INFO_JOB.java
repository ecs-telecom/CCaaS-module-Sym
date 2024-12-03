package com.ecstel.sym.job;

import com.ecstel.sym.config.BatchListConfig;
import com.ecstel.sym.service.InfoService;
import com.ecstel.sym.service.RealTimeService;
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
public class INFO_JOB extends QuartzJobBean implements InterruptableJob {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private BatchListConfig BatchListConfig;

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// Optional: 잡을 중지해야 할 경우 구현
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

		JobDataMap jobDataMap = context.getMergedJobDataMap();

		BatchInfo batchInfo = (BatchInfo) jobDataMap.get("batchInfo");

		CompletableFuture<Integer> U_SERVICE = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getU_SERVICE()){
					U_SERVICE(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> U_CAMPAIGNS = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getU_CAMPAIGNS()){
					U_CAMPAIGNS(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}

			} catch (Exception e) {
				log.error("Error processing batch U_CAMPAIGNS: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> U_ACW = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getU_ACW()) {
					U_ACW(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_ACW: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> U_AGENT = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getU_AGENT()){
					U_AGENT(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}

			} catch (Exception e) {
				log.error("Error processing batch U_AGENT: {}", e);
			}
			return processed ;
		});

		CompletableFuture<Integer> U_NRSN = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getU_NRSN()){
					U_NRSN(batchInfo);
					processed = 1;
				}else{
					processed = 0;
				}

			} catch (Exception e) {
				log.error("Error processing batch U_NRSN: {}", e);
			}
			return processed;
		});


		CompletableFuture<Integer> U_POC = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getU_POC()){
					U_POC(batchInfo); // 실제 U_POC 메소드 호출
					processed = 1;
				}else{
					processed = 0;
				}

			} catch (Exception e) {
				log.error("Error processing batch U_POC: {}", e);
			}
			return processed; // 결과 반환
		});

		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(U_SERVICE, U_CAMPAIGNS, U_ACW, U_AGENT, U_NRSN,  U_POC);

		combinedFuture.handle((result, throwable) -> {
			if (throwable != null) {
				log.error("Error occurred during batch processing: {}", throwable.getMessage());
			} else {
				try {
					Integer pocResult = U_POC.get();
					Integer serviceResult = U_SERVICE.get();
					Integer campaignsResult = U_CAMPAIGNS.get();
					Integer acwResult = U_ACW.get();
					Integer nrsnResult = U_NRSN.get();
					Integer AgentResult = U_AGENT.get();

					log.info("Batch processing completed successfully. Results: U_SERVICE={}, U_CAMPAIGNS={}, U_POC={}, U_ACW={}, U_NRSN={}, U_AGENT={}", serviceResult, campaignsResult, pocResult,acwResult,nrsnResult,AgentResult);
				} catch (InterruptedException | ExecutionException e) {
					log.error("Error retrieving results: {}", e.getMessage());
				}
			}
			return null; // handle 메소드는 T 결과를 반환하므로 null을 반환
		});
	}

	private void U_SERVICE(BatchInfo batchInfo) throws Exception {


		InfoService infoService = getInfoService(batchInfo);
		if (infoService != null) {
			infoService.getU_Service(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void U_CAMPAIGNS(BatchInfo batchInfo) throws Exception {
		InfoService infoService = getInfoService(batchInfo);
		if (infoService != null) {
			infoService.getU_Campagin(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void U_ACW(BatchInfo batchInfo) throws Exception {
		InfoService infoService = getInfoService(batchInfo);
		if (infoService != null) {
			infoService.getU_Acw(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void U_AGENT(BatchInfo batchInfo) throws Exception {
		InfoService infoService = getInfoService(batchInfo);
		if (infoService != null) {
			infoService.getU_Agent(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void U_NRSN(BatchInfo batchInfo) throws Exception {
		InfoService infoService = getInfoService(batchInfo);
		if (infoService != null) {
			infoService.getU_Nrsn(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}
	private int U_POC(BatchInfo batchInfo) throws Exception {
		InfoService infoService = getInfoService(batchInfo);
		if (infoService != null) {
			infoService.getU_Poc(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
		return 1;
	}

	private InfoService getInfoService(BatchInfo batchInfo) {
		switch (batchInfo.getContactCenterType()) {
			case "NICE":
				return applicationContext.getBean("niceInfoService", InfoService.class);
			case "BRIGHT_PATTERN":
				return applicationContext.getBean("bpIntoService", InfoService.class);
			default:
				log.warn("Unsupported Contact Center Type: {}", batchInfo.getContactCenterType());
				return null;
		}
	}
}