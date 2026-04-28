package org.giglab.live.application.command.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.junit.jupiter.api.Test;

class LeaveRoomTest {

  private final LeaveRoom handler = new LeaveRoom();

  @Test
  void actionShouldReturnChatLeave() {
    assertThat(handler.action()).isEqualTo(ActionType.CHAT_LEAVE);
  }

  @Test
  void executeShouldReturnResponseWithSameRoomAndActor() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.LEAVE", actor, Map.of());

    // When
    ActionResponse res = handler.execute(req);

    // Then
    assertThat(res.roomId()).isEqualTo("ROOM_1");
    assertThat(res.action()).isEqualTo("CHAT.LEAVE");
    assertThat(res.actor()).isEqualTo(actor);
    assertThat(res.sentAt()).isNotNull();
  }

  @Test
  void executeShouldPassThroughPayload() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    Map<String, Object> payload = Map.of("extra", "data");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.LEAVE", actor, payload);

    // When
    ActionResponse res = handler.execute(req);

    // Then
    assertThat(res.payload()).isEqualTo(payload);
  }
}
