package org.giglab.live.analytics.api.application;

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

  public GetFunnelResult getFunnel(String roomId) {
    return getFunnelUseCase.execute(roomId);
  }

  public GetViewerResult getViewer(String roomId) {
    return getViewerUseCase.execute(roomId);
  }

  public List<GetUtmResult> getUtm(String roomId) {
    return getUtmUseCase.execute(roomId);
  }
}
