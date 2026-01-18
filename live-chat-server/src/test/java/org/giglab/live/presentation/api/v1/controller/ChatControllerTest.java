package org.giglab.live.presentation.api.v1.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import org.giglab.live.application.dto.ChatMessageRequest;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

  @Mock private SimpMessageSendingOperations operations;

  @InjectMocks private ChatController chatController;

  @Test
  @DisplayName("메시지 전송 성공 - 올바른 destination으로 브로드캐스트")
  void sendMessageSuccessBroadcastsToCorrectDestination() {
    // given
    ChatMessageRequest request = new ChatMessageRequest("ROOM_1", "USER_1", "test-user", "안녕하세요");

    // when
    ChatMessageResponse response = chatController.sendMessage(request);

    // then
    verify(operations).convertAndSend(eq("/sub/channel/1"), any(ChatMessageResponse.class));
  }

  @Test
  @DisplayName("메시지 전송 성공 - 응답 DTO에 sentAt 포함")
  void sendMessageSuccessReturnsResponseWithSentAt() {
    // given
    ChatMessageRequest request = new ChatMessageRequest("ROOM_1", "USER_1", "jake", "테스트 메시지");

    // when
    ChatMessageResponse response = chatController.sendMessage(request);

    // then
    assertThat(response.getRoomId()).isEqualTo("ROOM_1");
    assertThat(response.getSender()).isEqualTo("jake");
    assertThat(response.getMessage()).isEqualTo("테스트 메시지");
    assertThat(response.getSentAt()).isNotNull();
    assertThat(response.getSentAt()).isInstanceOf(Instant.class);
  }

  @Test
  @DisplayName("메시지 전송 성공 - 채널별로 다른 destination으로 브로드캐스트")
  void sendMessageSuccessBroadcastsToChannelSpecificDestination() {
    // given
    ChatMessageRequest request1 = new ChatMessageRequest("ROOM_1", "USER_1", "user1", "채널1 메시지");
    ChatMessageRequest request2 = new ChatMessageRequest("ROOM_2", "USER_1", "user2", "채널2 메시지");

    // when
    chatController.sendMessage(request1);
    chatController.sendMessage(request2);

    // then
    verify(operations).convertAndSend(eq("/sub/room/ROOM_1"), any(ChatMessageResponse.class));
    verify(operations).convertAndSend(eq("/sub/room/ROOM_2"), any(ChatMessageResponse.class));
  }
}
