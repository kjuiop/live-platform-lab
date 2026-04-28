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
import org.giglab.live.application.port.persistence.RoomInsightPort;
import org.giglab.live.application.port.persistence.RoomPort;
import org.giglab.live.domain.model.Room;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private static final int MAX_SIZE = 20;

  private final RoomInsightPort roomInsightPort;
  private final RoomPort roomPort;

  public CreateRoomResponse createRoom(CreateRoomRequest request) {
    Room room = Room.create(request.getTitle());
    Room saved = roomPort.save(room);
    return new CreateRoomResponse(
        saved.getRoomId(), saved.getTitle(), saved.getCreatedAt(), saved.getUpdatedAt());
  }

  public void deleteRoom(String roomId) {
    roomPort.deleteById(roomId);
  }

  public ChatRoomInsightResponse getInsight(String roomId) {
    return ChatRoomInsightResponse.of(
        roomInsightPort.aggregateViewerStats(roomId),
        roomInsightPort.aggregateChatStats(roomId),
        roomInsightPort.findRawMessages(roomId));
  }

  public RoomStatsResponse getStats(String roomId) {
    return RoomStatsResponse.of(
        roomInsightPort.aggregateChatStats(roomId), roomInsightPort.aggregateViewerStats(roomId));
  }

  public List<GetRoomResponse> getRooms(int size) {
    int validSize = Math.min(MAX_SIZE, size);
    List<String> roomIds = roomPort.findLatestRoomIds(validSize);
    if (roomIds.isEmpty()) {
      return Collections.emptyList();
    }

    return roomPort.getRoomsByIds(roomIds).map(GetRoomResponse::from).collect(Collectors.toList());
  }
}
