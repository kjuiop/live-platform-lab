package org.giglab.live.application.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.report.ChatRoomInsightResponse;
import org.giglab.live.application.dto.room.CreateRoomRequest;
import org.giglab.live.application.dto.room.CreateRoomResponse;
import org.giglab.live.application.dto.room.GetRoomResponse;
import org.giglab.live.application.dto.stats.RoomStatsResponse;
import org.giglab.live.application.port.persistence.RoomQueryPort;
import org.giglab.live.application.port.persistence.RoomStorePort;
import org.giglab.live.domain.model.Room;
import org.giglab.live.infrastructure.mongo.MongoChatInsightRepository;
import org.giglab.live.infrastructure.mongo.aggregator.ChatMessageAggregator;
import org.giglab.live.infrastructure.mongo.aggregator.ViewerSessionAggregator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private static final int MAX_SIZE = 20;

  private final ViewerSessionAggregator viewerSessionAggregator;
  private final ChatMessageAggregator chatMessageAggregator;
  private final MongoChatInsightRepository chatInsightRepository;
  private final RoomQueryPort roomQueryPort;
  private final RoomStorePort roomStorePort;

  public CreateRoomResponse createRoom(CreateRoomRequest request) {
    Room room = Room.create(request.getTitle());
    Room saved = roomStorePort.save(room);
    return new CreateRoomResponse(
        saved.getRoomId(), saved.getTitle(), saved.getCreatedAt(), saved.getUpdatedAt());
  }

  public void deleteRoom(String roomId) {
    roomStorePort.deleteById(roomId);
  }

  public ChatRoomInsightResponse getInsight(String roomId) {
    return ChatRoomInsightResponse.of(
        viewerSessionAggregator.aggregate(roomId),
        chatMessageAggregator.aggregate(roomId),
        chatInsightRepository.findRawMessages(roomId));
  }

  public RoomStatsResponse getStats(String roomId) {
    return RoomStatsResponse.of(
        chatMessageAggregator.aggregate(roomId), viewerSessionAggregator.aggregate(roomId));
  }

  public List<GetRoomResponse> getRooms(int size) {
    int validSize = Math.min(MAX_SIZE, size);
    List<String> roomIds = roomQueryPort.findLatestRoomIds(validSize);
    if (roomIds.isEmpty()) {
      return Collections.emptyList();
    }

    return roomQueryPort
        .getRoomsByIds(roomIds)
        .map(GetRoomResponse::from)
        .collect(Collectors.toList());
  }
}
