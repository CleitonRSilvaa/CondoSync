package com.CondoSync.Configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduledConfig {
    @Value("${scheduled.task.fixedRate.minutes}")
    private long fixedRateMinutes;

    @Bean
    public long fixedRateMillis() {
        return fixedRateMinutes * 60 * 1000;
    }
}
