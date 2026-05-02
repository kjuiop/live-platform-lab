package org.giglab.live.collector.domain.model;

import java.time.Instant;
import java.util.Map;
import org.giglab.live.collector.presentation.dto.EventRequest;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Event(
    String eventId,
    String eventType,
    Instant occurredAt,
    String sessionId,
    String userId,
    String userName,
    String deviceId,
    String roomId,
    String osType,
    String appVersion,
    String utmSource,
    String utmCampaign,
    String utmMedium,
    String ip,
    Map<String, Object> properties) {
  public static Event from(EventRequest request, String ip) {
    return new Event(
        java.util.UUID.randomUUID().toString(),
        request.eventType(),
        Instant.now(),
        request.sessionId(),
        request.userId(),
        request.userName(),
        request.deviceId(),
        request.roomId(),
        request.osType() != null ? request.osType() : "",
        request.appVersion(),
        request.utmSource(),
        request.utmCampaign(),
        request.utmMedium(),
        ip,
        request.properties() != null ? request.properties() : Map.of());
  }
}
