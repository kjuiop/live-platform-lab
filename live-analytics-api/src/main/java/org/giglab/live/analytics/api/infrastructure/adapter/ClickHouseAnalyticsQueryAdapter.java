package org.giglab.live.analytics.api.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.giglab.live.analytics.api.infrastructure.clickhouse.FunnelClickHouseRepository;
import org.giglab.live.analytics.api.infrastructure.clickhouse.UtmClickHouseRepository;
import org.giglab.live.analytics.api.infrastructure.clickhouse.ViewerClickHouseRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickHouseAnalyticsQueryAdapter implements AnalyticsQueryPort {

  private final FunnelClickHouseRepository funnelRepository;
  private final ViewerClickHouseRepository viewerRepository;
  private final UtmClickHouseRepository utmRepository;

  @Override
  public GetFunnelResult getFunnelByRoomId(String roomId) {
    return funnelRepository.getFunnelByRoomId(roomId);
  }

  @Override
  public GetViewerResult getViewerByRoomId(String roomId) {
    return viewerRepository.getViewerByRoomId(roomId);
  }

  @Override
  public List<GetUtmResult> getUtmByRoomId(String roomId) {
    return utmRepository.getUtmByRoomId(roomId);
  }
}
