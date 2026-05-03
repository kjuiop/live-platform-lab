package org.giglab.live.analytics.api.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetFunnelUseCase {

  private final AnalyticsQueryPort analyticsQueryPort;

  public GetFunnelResult execute(String roomId) {
    return analyticsQueryPort.getFunnelByRoomId(roomId);
  }
}
