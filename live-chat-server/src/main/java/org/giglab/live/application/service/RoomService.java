package org.giglab.live.application.service;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.dto.CreateRoomRequest;
import org.giglab.live.application.dto.CreateRoomResponse;
import org.giglab.live.application.dto.GetRoomResponse;
import org.giglab.live.domain.model.Room;
import org.giglab.live.domain.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;

  public CreateRoomResponse createRoom(CreateRoomRequest request) {
    Room room = Room.create(request.getTitle());
    Room saved = roomRepository.save(room);
    return new CreateRoomResponse(
      saved.getRoomId(),
      saved.getTitle(),
      saved.getCreatedAt(),
      saved.getUpdatedAt()
    );
  }

  public List<GetRoomResponse> getRooms(int size) {
    List<String> roomIds = roomRepository.findLatestRoomIds(size);
    if (roomIds.isEmpty()) {
      return Collections.emptyList();
    }

    return roomRepository.getRoomsByIds(roomIds)
      .map(GetRoomResponse::from)
      .collect(Collectors.toList());
  }
}
