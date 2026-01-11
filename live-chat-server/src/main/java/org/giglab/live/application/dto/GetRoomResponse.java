package org.giglab.live.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.giglab.live.domain.model.Room;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
@AllArgsConstructor
public class GetRoomResponse {

  private String roomId;
  private String title;

  public static GetRoomResponse from(Room room) {
    return new GetRoomResponse(
      room.getRoomId(),
      room.getTitle()
    );
  }
}
