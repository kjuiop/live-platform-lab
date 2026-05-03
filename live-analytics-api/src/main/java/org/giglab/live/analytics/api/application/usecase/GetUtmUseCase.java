package org.giglab.live.analytics.api.application.usecase;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUtmUseCase {

  private final AnalyticsQueryPort analyticsQueryPort;

  public List<GetUtmResult> execute(String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    return analyticsQueryPort.getUtmByRoomId(roomId, startAt, endAt);
  }
}
