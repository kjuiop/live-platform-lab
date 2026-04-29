package org.giglab.live.commerce.core.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.temperature")
public record AiTemperatureProperties(
    double classifier, double faq, double report, double simulation) {}
