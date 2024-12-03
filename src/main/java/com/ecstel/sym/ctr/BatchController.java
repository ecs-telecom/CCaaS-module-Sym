package com.ecstel.sym.ctr;

import com.ecstel.sym.auth.AuthTokenInitializer;
import com.ecstel.sym.config.BatchStatConfig;
import com.ecstel.sym.job.*;
import com.ecstel.sym.config.BatchTimeConfig;
import com.ecstel.sym.mapper.ccaas.CCaaSDbMapper;
import com.ecstel.sym.service.InitService;
import com.ecstel.sym.service.ServiceInfoManager;
import com.ecstel.sym.utils.DataMap;
import com.ecstel.sym.vo.BatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;

@Slf4j
@Controller
public class BatchController {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private BatchTimeConfig batchTimeConfig;

    @Autowired
    private BatchStatConfig batchStatConfig;

    @Value("${sym.company-code}")
    private String companyCode;

    @Value("${nice.global-production-url}")
    private String globalProductionUrl;

    @Autowired
    private BatchInfo BatchInfo;

    @Autowired
    private InitService initService;

    @Autowired
    private ServiceInfoManager serviceInfoManager;

    @Autowired
    private CCaaSDbMapper CCaaSMapper;

    @PostConstruct
    public void start() throws IOException {
        DataMap config = new DataMap();
        config.put("companyCode", companyCode);
        List<Map<String, Object>> companyInfo = loadConfigurationFromDB(config);

        if (companyInfo.size() == 0) {
            log.error("companyInfo is null");
        } else {

            BatchInfo.setCompanyCode(companyCode);
            BatchInfo.setCompanyName("");
            BatchInfo.setIflag(batchStatConfig.getIflag());
            BatchInfo.setI30flag(batchStatConfig.getI30flag());
            BatchInfo.setHflag(batchStatConfig.getHflag());
            BatchInfo.setDflag(batchStatConfig.getDflag());
            BatchInfo.setMflag(batchStatConfig.getMflag());
            BatchInfo.setCompanyId(Integer.parseInt(companyInfo.get(0).get("companyId").toString()));
            BatchInfo.setContactCenterType(companyInfo.get(0).get("contactCenterType").toString());
            BatchInfo.setCcTenantUrl(companyInfo.get(0).get("ccTenantUrl").toString());
            BatchInfo.setCcTeantId(companyInfo.get(0).get("ccTenantId").toString());
            BatchInfo.setGrantType(companyInfo.get(0).get("grantType").toString());
            BatchInfo.setClientId(companyInfo.get(0).get("clientId").toString());
            BatchInfo.setClientSecret(companyInfo.get(0).get("clientSecret").toString());
            BatchInfo.setGlobalProductionUrl(globalProductionUrl);
            BatchInfo.setSchema(companyInfo.get(0).get("databaseSchema").toString());

            int ret = AuthTokenInitializer.initAuthToken(BatchInfo);
            System.out.println("ret : " + ret);


            DataMap paramMap = new DataMap();
            paramMap.put("companyId",BatchInfo.getCompanyId());
            List<Map<String, Object>> CCaaSServiceInfo =  CCaaSMapper.setlectUserviceData(paramMap);

            serviceInfoManager.clearServiceInfoList();
            for (Map<String, Object> serviceInfo : CCaaSServiceInfo) {
                serviceInfoManager.addServiceInfo(serviceInfo);
            }



            // 첫 번째 Job 설정 (15분마다 실행)
            JobDetail firstJobDetail = buildJobDetail(INFO_JOB.class, "INFO", "INFO", BatchInfo);
            Trigger firstJobTrigger = buildJobTrigger(batchTimeConfig.getInfoSchedule());
            scheduleJob(firstJobDetail, firstJobTrigger, "INFO");

            // 두 번째 Job 설정 (매 시각의 5, 20, 35, 50분에 실행)
            JobDetail secondJobDetail = buildJobDetail(HRD_JOB.class, "STAT", "STAT", BatchInfo);
            //Trigger secondJobTrigger = buildJobTrigger("0 5,20,35,50 * * * ?");
            if(batchTimeConfig.getStatSchedule().equals("")){
                Trigger secondJobTrigger = buildJobTrigger("0 5,16,35,50 * * * ?");
                scheduleJob(secondJobDetail, secondJobTrigger, "STAT");
            }else{
                Trigger secondJobTrigger = buildJobTrigger(batchTimeConfig.getStatSchedule());
                scheduleJob(secondJobDetail, secondJobTrigger, "STAT");
            }

            JobDetail thirdJobDetail = buildJobDetail(EVT_JOB.class, "EVT", "EVT", BatchInfo);
            Trigger thirdJobTrigger = buildJobTrigger(batchTimeConfig.getEvtSchedule());
            scheduleJob(thirdJobDetail, thirdJobTrigger, "EVT");

            JobDetail fourJobDetail = buildJobDetail(REAL_TIME_JOB.class, "REAL", "REAL", BatchInfo);
            Trigger fourJobTrigger = buildJobTrigger(batchTimeConfig.getRealSchedule());
            scheduleJob(fourJobDetail, fourJobTrigger, "REAL");

            JobDetail fiveJobDetail = buildJobDetail(DAILY_JOB.class, "DAILY", "DAILY", BatchInfo);
            Trigger fiveJobTrigger = buildJobTrigger(batchTimeConfig.getDailySchedule());
            scheduleJob(fiveJobDetail, fiveJobTrigger, "DAILY");
        }
    }

    private List<Map<String, Object>> loadConfigurationFromDB(DataMap config) {
        List<Map<String, Object>> dbConfigs = initService.loadConfigurationFromDB(config);
        return dbConfigs;
    }

    private void scheduleJob(JobDetail jobDetail, Trigger jobTrigger, String jobName) {
        try {
            scheduler.scheduleJob(jobDetail, jobTrigger);
        } catch (SchedulerException e) {
            log.error("Error scheduling {}", jobName, e);
        }
    }

    public Trigger buildJobTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp))
                .build();
    }

    public JobDetail buildJobDetail(Class<? extends Job> job, String name, String group, BatchInfo batchInfo) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("batchInfo", batchInfo); // BatchInfo 객체를 직접 저장

        return newJob(job)
                .withIdentity(name, group)
                .usingJobData(jobDataMap) // JobDataMap을 사용하여 JobDetail 생성
                .build();
    }
}
