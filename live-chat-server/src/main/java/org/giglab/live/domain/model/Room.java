package org.giglab.live.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.giglab.live.domain.model.type.RoomStatus;

import java.time.LocalDateTime;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Builder
@Getter
public class Room {

  private static final int MAX_TITLE_LENGTH = 50;

  private String roomId;

  private String title;

  @Builder.Default
  private RoomStatus status = RoomStatus.ACTIVE;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @JsonCreator
  public Room(
    @JsonProperty("roomId") String roomId,
    @JsonProperty("title") String title,
    @JsonProperty("status") RoomStatus status,
    @JsonProperty("createdAt") LocalDateTime createdAt,
    @JsonProperty("updatedAt") LocalDateTime updatedAt
  ) {
    this.roomId = roomId;
    this.title = title;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Room create(String title) {
    validateTitle(title);
    return Room.builder()
      .roomId(RoomIdGenerator.generate())
      .title(title)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
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
