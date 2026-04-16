package org.giglab.live.application.command.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.junit.jupiter.api.Test;

class SendMessageTest {

  private final SendMessage handler = new SendMessage();

  @Test
  void actionShouldReturnChatMessage() {
    assertThat(handler.action()).isEqualTo(ActionType.CHAT_MESSAGE);
  }

  @Test
  void executeShouldReturnResponseWithMessage() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.MESSAGE", actor, Map.of("message", "안녕"));

    // When
    ActionResponse res = handler.execute(req);

    // Then
    assertThat(res.roomId()).isEqualTo("ROOM_1");
    assertThat(res.action()).isEqualTo("CHAT.MESSAGE");
    assertThat(res.actor()).isEqualTo(actor);
    assertThat(res.payload()).containsEntry("message", "안녕");
    assertThat(res.sentAt()).isNotNull();
  }

  @Test
  void executeShouldThrowWhenPayloadIsNull() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.MESSAGE", actor, null);

    // When & Then
    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.message is required");
  }

  @Test
  void executeShouldThrowWhenMessageIsBlank() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.MESSAGE", actor, Map.of("message", "  "));

    // When & Then
    assertThatThrownBy(() -> handler.execute(req))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("payload.message is required");
  }
}
