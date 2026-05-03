package org.giglab.live.analytics.api.application.usecase;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFunnelUseCase {

  private final AnalyticsQueryPort analyticsQueryPort;

  public GetFunnelResult execute(String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    return analyticsQueryPort.getFunnelByRoomId(roomId, startAt, endAt);
  }
}
