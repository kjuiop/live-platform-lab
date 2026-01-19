package org.giglab.live.application.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.room.CreateRoomRequest;
import org.giglab.live.application.dto.room.CreateRoomResponse;
import org.giglab.live.application.dto.room.GetRoomResponse;
import org.giglab.live.domain.model.Room;
import org.giglab.live.domain.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

  private static final int MAX_SIZE = 20;

  private final RoomRepository roomRepository;

  public CreateRoomResponse createRoom(CreateRoomRequest request) {
    Room room = Room.create(request.getTitle());
    Room saved = roomRepository.save(room);
    return new CreateRoomResponse(
        saved.getRoomId(), saved.getTitle(), saved.getCreatedAt(), saved.getUpdatedAt());
  }

  public List<GetRoomResponse> getRooms(int size) {
    int validSize = Math.min(MAX_SIZE, size);
    List<String> roomIds = roomRepository.findLatestRoomIds(validSize);
    if (roomIds.isEmpty()) {
      return Collections.emptyList();
    }

    return roomRepository
        .getRoomsByIds(roomIds)
        .map(GetRoomResponse::from)
        .collect(Collectors.toList());
  }
}
