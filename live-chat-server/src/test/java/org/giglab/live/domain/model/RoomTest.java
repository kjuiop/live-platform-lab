package org.giglab.live.domain.model;

import org.giglab.live.domain.model.type.RoomStatus;
import org.giglab.live.presentation.api.error.exception.InvalidRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
class RoomTest {

  @Test
  @DisplayName("채팅방 생성")
  void createRoom() {

    // given
    String title = "test broadcast";

    // when
    Room room = Room.create(title);

    // then
    assertThat(room.getRoomId()).isNotNull();
    assertThat(room.getRoomId()).startsWith("ROOM");
    assertThat(room.getTitle()).isEqualTo(title);
    assertThat(room.getStatus()).isEqualTo(RoomStatus.ACTIVE);
  }

  @Test
  @DisplayName("채팅방 제목 길이 초과 시 예외 발생")
  void createRoom_TitleLengthExceeded() {
    // given
    String title = "A".repeat(51);

    // when & then
    assertThatThrownBy(() -> Room.create(title))
      .isInstanceOf(InvalidRequestException.class)
      .hasMessageContaining("Title cannot exceed 50 characters");
  }
}
