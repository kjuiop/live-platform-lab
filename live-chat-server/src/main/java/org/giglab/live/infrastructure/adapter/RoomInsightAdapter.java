package org.giglab.live.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.stats.ChatStats;
import org.giglab.live.application.dto.stats.ViewerStats;
import org.giglab.live.application.port.persistence.RoomInsightPort;
import org.giglab.live.infrastructure.mongo.MongoChatInsightRepository;
import org.giglab.live.infrastructure.mongo.aggregator.ChatMessageAggregator;
import org.giglab.live.infrastructure.mongo.aggregator.ViewerSessionAggregator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomInsightAdapter implements RoomInsightPort {

  private final ViewerSessionAggregator viewerSessionAggregator;
  private final ChatMessageAggregator chatMessageAggregator;
  private final MongoChatInsightRepository chatInsightRepository;

  @Override
  public ViewerStats aggregateViewerStats(String roomId) {
    return viewerSessionAggregator.aggregate(roomId);
  }

  @Override
  public ChatStats aggregateChatStats(String roomId) {
    return chatMessageAggregator.aggregate(roomId);
  }

  @Override
  public List<String> findRawMessages(String roomId) {
    return chatInsightRepository.findRawMessages(roomId);
  }
}
