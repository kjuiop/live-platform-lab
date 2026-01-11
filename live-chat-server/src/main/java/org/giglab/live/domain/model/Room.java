package org.giglab.live.domain.model;

import lombok.Builder;
import lombok.Getter;
import org.giglab.live.domain.model.type.RoomStatus;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Builder
@Getter
public class Room {

  private static final int MAX_TITLE_LENGTH = 20;

  private String roomId;

  private String title;

  @Builder.Default
  private RoomStatus status = RoomStatus.ACTIVE;

  public static Room create(String title) {
    validateTitle(title);
    return Room.builder()
      .roomId(RoomIdGenerator.generate())
      .title(title)
      .build();
  }

  private static void validateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Title cannot be null or empty");
    }
    if (title.length() > MAX_TITLE_LENGTH) {
      throw new IllegalArgumentException(
        String.format("Title cannot exceed %d characters. Current length: %d", 
          MAX_TITLE_LENGTH, title.length())
      );
    }
  }
}
