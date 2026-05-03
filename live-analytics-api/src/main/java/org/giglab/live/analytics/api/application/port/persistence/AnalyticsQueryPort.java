package org.giglab.live.analytics.api.application.port.persistence;

import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;

public interface AnalyticsQueryPort {

  GetFunnelResult getFunnelByRoomId(String roomId);

  GetViewerResult getViewerByRoomId(String roomId);
}
