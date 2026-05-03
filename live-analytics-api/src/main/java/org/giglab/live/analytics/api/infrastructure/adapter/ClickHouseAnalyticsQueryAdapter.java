package org.giglab.live.analytics.api.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.giglab.live.analytics.api.infrastructure.clickhouse.FunnelClickHouseRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickHouseAnalyticsQueryAdapter implements AnalyticsQueryPort {

  private final FunnelClickHouseRepository queryRepository;

  @Override
  public GetFunnelResult getFunnelByRoomId(String roomId) {
    return queryRepository.getFunnelByRoomId(roomId);
  }
}
