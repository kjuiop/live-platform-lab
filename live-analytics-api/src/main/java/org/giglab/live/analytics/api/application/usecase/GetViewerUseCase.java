package org.giglab.live.analytics.api.application.usecase;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetViewerUseCase {

  private final AnalyticsQueryPort analyticsQueryPort;

  public GetViewerResult execute(String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    return analyticsQueryPort.getViewerByRoomId(roomId, startAt, endAt);
  }
}
