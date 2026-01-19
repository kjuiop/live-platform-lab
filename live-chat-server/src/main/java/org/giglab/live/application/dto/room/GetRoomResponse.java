package org.giglab.live.application.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.giglab.live.domain.model.Room;

@Getter
@AllArgsConstructor
public class GetRoomResponse {

  private String roomId;
  private String title;

  public static GetRoomResponse from(Room room) {
    return new GetRoomResponse(room.getRoomId(), room.getTitle());
  }
}
