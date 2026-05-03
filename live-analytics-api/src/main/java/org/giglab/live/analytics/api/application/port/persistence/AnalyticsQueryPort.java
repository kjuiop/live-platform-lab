package org.giglab.live.analytics.api.application.port.persistence;

import org.giglab.live.analytics.api.application.dto.GetFunnelResult;

public interface AnalyticsQueryPort {

  GetFunnelResult getFunnelByRoomId(String roomId);
}
