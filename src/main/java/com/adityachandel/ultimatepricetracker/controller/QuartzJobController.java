package com.adityachandel.ultimatepricetracker.controller;

import com.adityachandel.ultimatepricetracker.model.api.dto.QuartzJobDTO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static java.time.ZoneId.systemDefault;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.Trigger.TriggerState.PAUSED;

@AllArgsConstructor
@RestController
@RequestMapping("/quartz-jobs")
public class QuartzJobController {

    private final Scheduler scheduler;

    @SneakyThrows
    @GetMapping()
    public List<QuartzJobDTO> getAllJobs() {
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
        List<QuartzJobDTO> quartzJobs = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(jobKey.getName()));
            QuartzJobDTO quartzJob = QuartzJobDTO.builder()
                    .jobName(trigger.getJobKey().getName())
                    .cron(trigger.getCronExpression())
                    .enabled(scheduler.getTriggerState(TriggerKey.triggerKey(jobKey.getName())) != PAUSED)
                    .build();
            quartzJobs.add(quartzJob);
        }
        return quartzJobs;
    }

    @GetMapping("/{jobId}")
    public QuartzJobDTO getJob(@PathVariable String jobId) throws SchedulerException {
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(jobId));
        return QuartzJobDTO.builder()
                .jobName(trigger.getJobKey().getName())
                .cron(trigger.getCronExpression())
                .enabled(scheduler.getTriggerState(TriggerKey.triggerKey(jobId)) != PAUSED)
                .build();
    }

    @PutMapping()
    public void updateJob(@RequestBody QuartzJobDTO quartzJob) throws SchedulerException {
        Trigger oldTrigger = scheduler.getTrigger(TriggerKey.triggerKey(quartzJob.getJobName()));
        TriggerBuilder triggerBuilder = oldTrigger.getTriggerBuilder();
        Trigger newTrigger = triggerBuilder.withSchedule(cronSchedule(quartzJob.getCron())
                        .withMisfireHandlingInstructionFireAndProceed()
                        .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                .build();
        scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
        if (quartzJob.isEnabled()) {
            scheduler.resumeTrigger(TriggerKey.triggerKey(quartzJob.getJobName()));
        } else {
            scheduler.pauseTrigger(TriggerKey.triggerKey(quartzJob.getJobName()));
        }
    }

}
