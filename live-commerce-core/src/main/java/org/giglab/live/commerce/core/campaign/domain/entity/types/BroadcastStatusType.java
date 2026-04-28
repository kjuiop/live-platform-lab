package org.giglab.live.commerce.core.campaign.domain.entity.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BroadcastStatusType {
  SCHEDULED("Scheduled", "방송 예정"),
  ON_AIR("OnAir", "방송 중"),
  ENDED("Ended", "방송 종료");

  private final String key;
  private final String description;
}
