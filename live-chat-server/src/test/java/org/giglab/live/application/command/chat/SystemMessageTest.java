package org.giglab.live.application.command.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.domain.exception.ChatDomainException;
import org.junit.jupiter.api.Test;

class SystemMessageTest {

  private final SystemMessage handler = new SystemMessage();

  @Test
  void actionShouldReturnChatSystem() {
    assertThat(handler.action()).isEqualTo(ActionType.CHAT_SYSTEM);
  }

  @Test
  void executeShouldReturnResponseWithMessage() {
    // Given
    ActionRequest req =
        new ActionRequest("ROOM_1", "CHAT.SYSTEM", null, Map.of("message", "공지입니다."));

    // When
    ActionResponse res = handler.execute(req);

    // Then
    assertThat(res.roomId()).isEqualTo("ROOM_1");
    assertThat(res.action()).isEqualTo("CHAT.SYSTEM");
    assertThat(res.payload()).containsEntry("message", "공지입니다.");
    assertThat(res.sentAt()).isNotNull();
  }

  @Test
  void executeShouldThrowWhenPayloadIsNull() {
    // Given
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.SYSTEM", null, null);

    // When & Then
    assertThatThrownBy(() -> handler.execute(req)).isInstanceOf(ChatDomainException.class);
  }

  @Test
  void executeShouldThrowWhenMessageIsBlank() {
    // Given
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.SYSTEM", null, Map.of("message", "  "));

    // When & Then
    assertThatThrownBy(() -> handler.execute(req)).isInstanceOf(ChatDomainException.class);
  }

  @Test
  void executeShouldThrowWhenMessageKeyMissing() {
    // Given
    Map<String, Object> payload = new HashMap<>();
    payload.put("message", null);
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.SYSTEM", null, payload);

    // When & Then
    assertThatThrownBy(() -> handler.execute(req)).isInstanceOf(ChatDomainException.class);
  }
}
