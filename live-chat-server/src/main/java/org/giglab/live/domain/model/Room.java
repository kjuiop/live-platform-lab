package org.giglab.live.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import org.giglab.live.domain.model.type.RoomStatus;
import org.giglab.live.presentation.api.error.exception.InvalidRequestException;

@Builder
@Getter
public class Room {

  private static final int MAX_TITLE_LENGTH = 50;

  private String roomId;

  private String title;

  @Builder.Default private RoomStatus status = RoomStatus.ACTIVE;

  private Instant createdAt;

  private Instant updatedAt;

  @JsonCreator
  public Room(
      @JsonProperty("roomId") String roomId,
      @JsonProperty("title") String title,
      @JsonProperty("status") RoomStatus status,
      @JsonProperty("createdAt") Instant createdAt,
      @JsonProperty("updatedAt") Instant updatedAt) {
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
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  private static void validateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new InvalidRequestException("Title cannot be null or empty");
    }
    if (title.length() > MAX_TITLE_LENGTH) {
      throw new InvalidRequestException(
          String.format(
              "Title cannot exceed %d characters. Current length: %d",
              MAX_TITLE_LENGTH, title.length()));
    }
  }
}
