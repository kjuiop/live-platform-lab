package org.giglab.live.application.dto.room;

import org.giglab.live.domain.model.Room;

public record GetRoomResponse(String roomId, String title) {

  public static GetRoomResponse from(Room room) {
    return new GetRoomResponse(room.getRoomId(), room.getTitle());
  }
}
