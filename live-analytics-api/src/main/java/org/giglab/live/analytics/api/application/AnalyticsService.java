package org.giglab.live.analytics.api.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.usecase.GetFunnelUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

  private final GetFunnelUseCase getFunnelUseCase;

  public GetFunnelResult getFunnel(String roomId) {
    return getFunnelUseCase.execute(roomId);
  }
}
