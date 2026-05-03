package org.giglab.live.analytics.api.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.port.persistence.AnalyticsQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUtmUseCase {

  private final AnalyticsQueryPort analyticsQueryPort;

  public List<GetUtmResult> execute(String roomId) {
    return analyticsQueryPort.getUtmByRoomId(roomId);
  }
}
