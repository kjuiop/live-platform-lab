package org.giglab.live.analytics.api.application.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record DateRange(LocalDateTime start, LocalDateTime end) {

  public static DateRange resolve(LocalDateTime startAt, LocalDateTime endAt) {
    LocalDateTime start =
        startAt != null ? startAt : LocalDateTime.now(ZoneOffset.UTC).minusDays(30);
    LocalDateTime end = endAt != null ? endAt : LocalDateTime.now(ZoneOffset.UTC);
    if (start.isAfter(end)) {
      throw new IllegalArgumentException("startAt must be before endAt");
    }
    return new DateRange(start, end);
  }
}
