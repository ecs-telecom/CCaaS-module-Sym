package com.ecstel.sym.job;

import com.ecstel.sym.service.DailyBatchService;
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

@Component
@Slf4j
public class DAILY_JOB extends QuartzJobBean implements InterruptableJob {
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

		String ymd = jobDataMap.getString("ymd");



		// BatchInfo 객체를 직접 읽어옴
		BatchInfo batchInfo = (BatchInfo) jobDataMap.get("batchInfo");

		// BatchInfo 사용
		String companyCode = batchInfo.getCompanyCode();

		// 두 개의 비동기 작업 실행
		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			try {
					DailyBatch(batchInfo,ymd);
			} catch (Exception e) {
				log.error("Error processing batch 1: {}", e.getMessage());
			}
		});


		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1);

		combinedFuture.whenComplete((result, throwable) -> {
			if (throwable != null) {
				log.error("Error occurred during batch processing: {}", throwable.getMessage());
			} else {
				log.info("Batch processing for both tasks completed successfully.");
			}
		});
	}

	private void DailyBatch(BatchInfo batchInfo,String ymd) throws Exception {

		DailyBatchService realTimeService = getRealTimeService(batchInfo);
		if (realTimeService != null) {
			realTimeService.getDailyBatch(batchInfo,ymd);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private DailyBatchService getRealTimeService(BatchInfo batchInfo) {
		switch (batchInfo.getContactCenterType()) {
			case "NICE":
				return applicationContext.getBean("niceDailyBatchService", DailyBatchService.class);
			case "BRIGHT_PATTERN":
				return applicationContext.getBean("bpDailyBatchService", DailyBatchService.class);
			default:
				log.warn("Unsupported Contact Center Type: {}", batchInfo.getContactCenterType());
				return null;
		}
	}


}