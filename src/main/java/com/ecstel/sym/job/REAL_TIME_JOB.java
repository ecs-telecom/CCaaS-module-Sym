package com.ecstel.sym.job;

import com.ecstel.sym.config.BatchListConfig;
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
public class REAL_TIME_JOB extends QuartzJobBean implements InterruptableJob {
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

		// BatchInfo 객체를 직접 읽어옴
		BatchInfo batchInfo = (BatchInfo) jobDataMap.get("batchInfo");

		// BatchInfo 사용
		String companyCode = batchInfo.getCompanyCode();

		CompletableFuture<Integer> R_AGT = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getR_AGENT()) {
					R_AGT(batchInfo);
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> R_SERVICE = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getR_SERVICE()) {
					R_SERVICE(batchInfo);
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> AGT_SERVICE_MAP = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getAGT_SERVICE_MAP()) {
					AGT_SERVICE_MAP(batchInfo);
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});

		CompletableFuture<Integer> R_CAMPAIGN = CompletableFuture.supplyAsync(() -> {
			Integer processed = 0;
			try {
				if(BatchListConfig.getR_CAMPAIGN()) {
					R_CAMPAIGN(batchInfo);
				}else{
					processed = 0;
				}
			} catch (Exception e) {
				log.error("Error processing batch U_SERVICE: {}", e);
			}
			return processed;
		});



		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(R_AGT, R_SERVICE);

		combinedFuture.handle((result, throwable) -> {
			if (throwable != null) {
				log.error("Error occurred during batch processing: {}", throwable.getMessage());
			} else {
				try {
					Integer rAgtResult = R_AGT.get();
					Integer rServiceResult = R_SERVICE.get();
					Integer agtServiceMapResult = AGT_SERVICE_MAP.get();
					Integer rCampaignResult = R_CAMPAIGN.get();

					log.info("Batch processing completed successfully. Results: R_AGT={}, R_SERVICE={}, AGT_SERVICE_MAP={} rCampaignResult={}", rAgtResult, rServiceResult,agtServiceMapResult,rCampaignResult);
				} catch (InterruptedException | ExecutionException e) {
					log.error("Error retrieving results: {}", e.getMessage());
				}
			}
			return null; // handle 메소드는 T 결과를 반환하므로 null을 반환
		});

	}

	private void R_AGT(BatchInfo batchInfo) throws Exception {


		RealTimeService realTimeService = getRealTimeService(batchInfo);
		if (realTimeService != null) {
			realTimeService.getRealTimeAgt(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void R_SERVICE(BatchInfo batchInfo) throws Exception {
		RealTimeService realTimeService = getRealTimeService(batchInfo);
		if (realTimeService != null) {
			realTimeService.getRealTimeService(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void R_CAMPAIGN(BatchInfo batchInfo) throws Exception {
		RealTimeService realTimeService = getRealTimeService(batchInfo);
		if (realTimeService != null) {
			realTimeService.getRealTimeCampaign(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void AGT_SERVICE_MAP(BatchInfo batchInfo) throws Exception {
		RealTimeService realTimeService = getRealTimeService(batchInfo);
		if (realTimeService != null) {
			realTimeService.getAgtServiceMap(batchInfo);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}



	private RealTimeService getRealTimeService(BatchInfo batchInfo) {
		switch (batchInfo.getContactCenterType()) {
			case "NICE":
				return applicationContext.getBean("niceRagtService", RealTimeService.class);
			case "BRIGHT_PATTERN":
				return applicationContext.getBean("bpRagtService", RealTimeService.class);
			default:
				log.warn("Unsupported Contact Center Type: {}", batchInfo.getContactCenterType());
				return null;
		}
	}


}