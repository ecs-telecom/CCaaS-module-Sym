package com.ecstel.sym.ctr;

import com.ecstel.sym.job.DAILY_JOB;
import com.ecstel.sym.vo.BatchInfo;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manual")
public class BatchManualController {

    @Autowired
    private Scheduler scheduler; // Quartz Scheduler


    @Autowired
    private BatchInfo batchInfo;

    @GetMapping
    public String triggerDailyJob(@RequestParam String ymd) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(DAILY_JOB.class)
                    .withIdentity("dailyJob", "batchJobs")
                    .usingJobData(createJobDataMap(batchInfo,ymd)) // 전달할 데이터 설정
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("dailyJobTrigger", "batchJobs")
                    .startNow() // 즉시 실행
                    .build();

            scheduler.scheduleJob(jobDetail, trigger); // Quartz Job 실행
            return "Job triggered successfully with ymd: " + ymd;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return "Failed to trigger job: " + e.getMessage();
        }
    }
    private JobDataMap createJobDataMap(BatchInfo batchInfo,String ymd) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("batchInfo", batchInfo); // BatchInfo 객체 전달
        jobDataMap.put("ymd", ymd); // BatchInfo 객체 전달
        return jobDataMap;
    }
}

