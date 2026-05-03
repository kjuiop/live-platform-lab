package org.giglab.live.analytics.api.application.port.persistence;

import java.time.LocalDateTime;
import java.util.List;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;

public interface AnalyticsQueryPort {

  GetFunnelResult getFunnelByRoomId(String roomId, LocalDateTime startAt, LocalDateTime endAt);

  GetViewerResult getViewerByRoomId(String roomId, LocalDateTime startAt, LocalDateTime endAt);

  List<GetUtmResult> getUtmByRoomId(String roomId, LocalDateTime startAt, LocalDateTime endAt);
}
