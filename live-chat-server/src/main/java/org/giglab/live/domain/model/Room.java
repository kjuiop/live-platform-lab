package org.giglab.live.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import org.giglab.live.domain.exception.RoomDomainException;
import org.giglab.live.domain.exception.RoomErrorCode;
import org.giglab.live.domain.model.type.RoomStatus;

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
      throw new RoomDomainException(RoomErrorCode.INVALID_TITLE, "제목은 비워둘 수 없습니다.");
    }
    if (title.length() > MAX_TITLE_LENGTH) {
      throw new RoomDomainException(
          RoomErrorCode.INVALID_TITLE,
          String.format("제목은 %d자를 초과할 수 없습니다. 현재 길이: %d", MAX_TITLE_LENGTH, title.length()));
    }
  }
}
