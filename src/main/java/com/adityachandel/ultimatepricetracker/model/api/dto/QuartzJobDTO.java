package com.adityachandel.ultimatepricetracker.model.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuartzJobDTO {
    private String jobName;
    private String cron;
    private boolean enabled;
}
