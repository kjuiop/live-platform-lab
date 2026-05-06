package org.giglab.live.application.command.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActiveBanner;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.dto.action.JoinRoomResponse;
import org.giglab.live.application.port.persistence.BannerStatePort;
import org.junit.jupiter.api.Test;

class JoinRoomTest {

  private final BannerStatePort bannerStatePort = mock(BannerStatePort.class);
  private final JoinRoom handler = new JoinRoom(bannerStatePort);

  @Test
  void actionShouldReturnChatJoin() {
    assertThat(handler.action()).isEqualTo(ActionType.CHAT_JOIN);
  }

  @Test
  void executeShouldReturnResponseWithSameRoomAndActor() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.JOIN", actor, Map.of());
    when(bannerStatePort.getActiveBanner("ROOM_1")).thenReturn(Optional.empty());

    // When
    JoinRoomResponse res = (JoinRoomResponse) handler.execute(req);

    // Then
    assertThat(res.roomId()).isEqualTo("ROOM_1");
    assertThat(res.action()).isEqualTo("CHAT.JOIN");
    assertThat(res.actor()).isEqualTo(actor);
    assertThat(res.sentAt()).isNotNull();
    assertThat(res.activeBanner()).isNull();
  }

  @Test
  void executeShouldIncludeActiveBannerWhenBannerExists() {
    // Given
    Actor actor = new Actor("u1", "user1@example.com", "사용자1");
    ActionRequest req = new ActionRequest("ROOM_1", "CHAT.JOIN", actor, Map.of());
    ActiveBanner activeBanner = new ActiveBanner(1001L, "나이키 에어맥스");
    when(bannerStatePort.getActiveBanner("ROOM_1")).thenReturn(Optional.of(activeBanner));

    // When
    JoinRoomResponse res = (JoinRoomResponse) handler.execute(req);

    // Then
    assertThat(res.activeBanner()).isEqualTo(activeBanner);
    assertThat(res.activeBanner().productId()).isEqualTo(1001L);
    assertThat(res.activeBanner().productName()).isEqualTo("나이키 에어맥스");
  }
}
