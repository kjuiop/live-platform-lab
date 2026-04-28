package org.giglab.live.application.port.persistence;

import java.util.List;
import org.giglab.live.application.dto.stats.ChatStats;
import org.giglab.live.application.dto.stats.ViewerStats;

public interface RoomInsightPort {

  ViewerStats aggregateViewerStats(String roomId);

  ChatStats aggregateChatStats(String roomId);

  List<String> findRawMessages(String roomId);
}
