package com.adityachandel.ultimatepricetracker.service.quartz;

import com.adityachandel.ultimatepricetracker.model.entity.StateEntity;
import com.adityachandel.ultimatepricetracker.model.enums.StateKey;
import com.adityachandel.ultimatepricetracker.model.enums.StoreType;
import com.adityachandel.ultimatepricetracker.repository.StateRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.TimeZone;

import static java.time.ZoneId.systemDefault;
import static org.quartz.CronScheduleBuilder.cronSchedule;

@Service
@AllArgsConstructor
public class JobScheduler {

    private final Scheduler scheduler;
    private final StateRepository stateRepository;

    @PostConstruct
    public void init() {
        Optional<StateEntity> alreadyInitiated = stateRepository.findById(StateKey.INIT_QUARTZ_JOBS.name());
        if (alreadyInitiated.isEmpty()) {
            setupInitialJobs();
            stateRepository.save(StateEntity.builder()
                    .name(StateKey.INIT_QUARTZ_JOBS.name())
                    .value("true")
                    .build());
        }
    }

    @SneakyThrows
    void setupInitialJobs() {
        for (StoreType storeType : StoreType.values()) {
            JobDetail jobDetail = JobBuilder.newJob(FetchPriceJob.class)
                    .withIdentity(storeType.name())
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(storeType.name())
                    .withSchedule(cronSchedule(storeType.getCron())
                            .withMisfireHandlingInstructionFireAndProceed()
                            .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

}
