package org.giglab.live.analytics.api.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;
import org.giglab.live.analytics.api.application.usecase.GetFunnelUseCase;
import org.giglab.live.analytics.api.application.usecase.GetUtmUseCase;
import org.giglab.live.analytics.api.application.usecase.GetViewerUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

  private final GetFunnelUseCase getFunnelUseCase;
  private final GetViewerUseCase getViewerUseCase;
  private final GetUtmUseCase getUtmUseCase;

  public GetFunnelResult getFunnel(String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    return getFunnelUseCase.execute(roomId, startAt, endAt);
  }

  public GetViewerResult getViewer(String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    return getViewerUseCase.execute(roomId, startAt, endAt);
  }

  public List<GetUtmResult> getUtm(String roomId, LocalDateTime startAt, LocalDateTime endAt) {
    return getUtmUseCase.execute(roomId, startAt, endAt);
  }
}
