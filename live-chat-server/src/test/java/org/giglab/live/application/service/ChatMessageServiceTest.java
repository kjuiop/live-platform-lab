package org.giglab.live.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.giglab.live.application.dto.ChatMessageResponse;
import org.giglab.live.domain.model.ChatMessage;
import org.giglab.live.domain.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

  @Mock private ChatMessageRepository chatMessageRepository;

  @InjectMocks private ChatMessageService chatMessageService;

  @Test
  void getRecentMessages_limitExceedsMax_clampsTo200() {
    // Given
    when(chatMessageRepository.findRecentByRoomId("ROOM_1", 200)).thenReturn(List.of());

    // When
    chatMessageService.getRecentMessages("ROOM_1", 999);

    // Then
    ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(chatMessageRepository)
        .findRecentByRoomId(org.mockito.ArgumentMatchers.eq("ROOM_1"), limitCaptor.capture());
    assertThat(limitCaptor.getValue()).isEqualTo(200);
  }

  @Test
  void getRecentMessages_limitZero_clampsTo1() {
    // Given
    when(chatMessageRepository.findRecentByRoomId("ROOM_1", 1)).thenReturn(List.of());

    // When
    chatMessageService.getRecentMessages("ROOM_1", 0);

    // Then
    ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(chatMessageRepository)
        .findRecentByRoomId(org.mockito.ArgumentMatchers.eq("ROOM_1"), limitCaptor.capture());
    assertThat(limitCaptor.getValue()).isEqualTo(1);
  }

  @Test
  void getRecentMessages_negativeLimit_clampsTo1() {
    // Given
    when(chatMessageRepository.findRecentByRoomId("ROOM_1", 1)).thenReturn(List.of());

    // When
    chatMessageService.getRecentMessages("ROOM_1", -10);

    // Then
    ArgumentCaptor<Integer> limitCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(chatMessageRepository)
        .findRecentByRoomId(org.mockito.ArgumentMatchers.eq("ROOM_1"), limitCaptor.capture());
    assertThat(limitCaptor.getValue()).isEqualTo(1);
  }

  @Test
  void getRecentMessages_returnsMessagesInAscendingOrder() {
    // Given — repository returns DESC order (most recent first)
    Instant older = Instant.parse("2024-01-01T10:00:00Z");
    Instant newer = Instant.parse("2024-01-01T11:00:00Z");

    List<ChatMessage> descResult = new ArrayList<>();
    descResult.add(buildMessage("msg2", "ROOM_1", newer));
    descResult.add(buildMessage("msg1", "ROOM_1", older));
    when(chatMessageRepository.findRecentByRoomId("ROOM_1", 2)).thenReturn(descResult);

    // When
    List<ChatMessageResponse> result = chatMessageService.getRecentMessages("ROOM_1", 2);

    // Then — service reverses to ASC order
    assertThat(result).hasSize(2);
    assertThat(result.get(0).sentAt()).isEqualTo(older);
    assertThat(result.get(1).sentAt()).isEqualTo(newer);
  }

  @Test
  void getRecentMessages_passesRoomIdToRepository() {
    // Given
    when(chatMessageRepository.findRecentByRoomId("ROOM_XYZ", 10)).thenReturn(List.of());

    // When
    chatMessageService.getRecentMessages("ROOM_XYZ", 10);

    // Then
    verify(chatMessageRepository).findRecentByRoomId("ROOM_XYZ", 10);
  }

  private ChatMessage buildMessage(String id, String roomId, Instant sentAt) {
    return ChatMessage.builder()
        .id(id)
        .roomId(roomId)
        .action("CHAT.MESSAGE")
        .senderNickname("사용자")
        .payload(Map.of("message", "안녕하세요"))
        .sentAt(sentAt)
        .build();
  }
}
