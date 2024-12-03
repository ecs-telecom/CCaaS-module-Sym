package com.ecstel.sym.job;

import com.ecstel.sym.service.EvtService;
import com.ecstel.sym.service.StatService;
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
public class HRD_JOB extends QuartzJobBean implements InterruptableJob {
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

		try {
			// STAT_H_SERVICE 작업 실행
			if (BatchListConfig.getSTAT_H_SERVICE()) {
				STAT_H_SERVICE(batchInfo);
			}

			// E_AGENT_STATUS 작업 실행
			if (BatchListConfig.getE_AGENT_STATUS()) {
				E_AGENT_STATUS(batchInfo);
				H_AGENT_STATUS(batchInfo);
				H_AGENT_INBOUND(batchInfo);
				H_AGENT_OUTBOUND(batchInfo);
				STAT_H_AGENT(batchInfo);
			}

			log.info("Batch processing completed successfully.");

		} catch (Exception e) {
			log.error("Error occurred during batch processing: {}", e.getMessage());
		}
	}

	private void H_AGENT_OUTBOUND(BatchInfo batchInfo) throws Exception {


		StatService statService = getInfoService(batchInfo);
		if (statService != null) {
			statService.getStatHAgentOutbound(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void H_AGENT_INBOUND(BatchInfo batchInfo) throws Exception {


		StatService statService = getInfoService(batchInfo);
		if (statService != null) {
			statService.getStatHAgentInbound(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void H_AGENT_STATUS(BatchInfo batchInfo) throws Exception {


		StatService statService = getInfoService(batchInfo);
		if (statService != null) {
			statService.getStatHAgentStatus(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void STAT_H_AGENT(BatchInfo batchInfo) throws Exception {


		StatService statService = getInfoService(batchInfo);
		if (statService != null) {
			statService.getStatHAgent(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void E_AGENT_STATUS(BatchInfo batchInfo) throws Exception {


		StatService statService = getInfoService(batchInfo);
		if (statService != null) {
			statService.getEAgentStatus(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private void STAT_H_SERVICE(BatchInfo batchInfo) throws Exception {


		StatService statService = getInfoService(batchInfo);
		if (statService != null) {
			statService.getStatHService(batchInfo,0);
		} else {
			log.warn("Unsupported contact center type: {}", batchInfo.getContactCenterType());
		}
	}

	private StatService getInfoService(BatchInfo batchInfo) {
		switch (batchInfo.getContactCenterType()) {
			case "NICE":
				return applicationContext.getBean("niceStatService", StatService.class);
			case "BRIGHT_PATTERN":
				return applicationContext.getBean("bpStatService", StatService.class);
			default:
				log.warn("Unsupported Contact Center Type: {}", batchInfo.getContactCenterType());
				return null;
		}
	}
}
