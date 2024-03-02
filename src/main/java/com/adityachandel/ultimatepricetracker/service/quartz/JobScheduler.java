package com.adityachandel.ultimatepricetracker.service.quartz;

import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.repository.StateRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneId.systemDefault;
import static org.quartz.CronScheduleBuilder.cronSchedule;

@Service
@AllArgsConstructor
public class JobScheduler {

    private final Scheduler scheduler;
    private final StateRepository stateRepository;

    @PostConstruct
    public void init() {
        initNewJobs();
    }

    @SneakyThrows
    void initNewJobs() {
        for (String storeName : Arrays.stream(StoreType.values()).map(StoreType::name).collect(Collectors.toSet())) {
            Set<String> jobs = scheduler.getJobKeys(GroupMatcher.anyGroup()).stream().map(Key::getName).collect(Collectors.toSet());
            if (!jobs.contains(storeName)) {
                JobDetail jobDetail = JobBuilder.newJob(FetchPriceJob.class)
                        .withIdentity(storeName)
                        .build();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(storeName)
                        .withSchedule(cronSchedule(StoreType.valueOf(storeName).getCron())
                                .withMisfireHandlingInstructionFireAndProceed()
                                .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
    }

}
