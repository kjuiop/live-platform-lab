package org.giglab.live.collector.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record EventRequest(
    @NotBlank String eventType,
    @NotBlank String sessionId,
    String userId,
    String userName,
    String deviceId,
    @NotBlank String roomId,
    String osType,
    String appVersion,
    String utmSource,
    String utmCampaign,
    String utmMedium,
    Map<String, Object> properties) {}
