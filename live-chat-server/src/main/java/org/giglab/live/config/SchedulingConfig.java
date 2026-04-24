package org.giglab.live.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true")
@EnableScheduling
@Configuration
public class SchedulingConfig {}
