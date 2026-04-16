package org.giglab.live.presentation.api.v1.controller.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.giglab.live.application.command.ActionDispatcher;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.Actor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@ExtendWith(MockitoExtension.class)
class RoomActionControllerTest {

  @Mock private ActionDispatcher dispatcher;

  @Mock private SimpMessageSendingOperations messaging;

  private RoomActionController controller;

  @BeforeEach
  void setUp() {
    controller = new RoomActionController(dispatcher, messaging);
  }

  @Test
  void handleShouldDispatchAndBroadcast() {
    // Given
    ActionRequest request = createMessageRequest("ROOM_1", "안녕하세요");
    ActionResponse response = createMessageResponse("ROOM_1", "안녕하세요");

    when(dispatcher.dispatch(request)).thenReturn(response);

    // When
    controller.handle(request);

    // Then
    verify(dispatcher).dispatch(request);
    verify(messaging).convertAndSend(eq("/sub/room/ROOM_1"), any(ActionResponse.class));
  }

  @Test
  void handleShouldBroadcastToCorrectRoom() {
    // Given
    String roomId = "ROOM_999";
    ActionRequest request = createMessageRequest(roomId, "테스트");
    ActionResponse response = createMessageResponse(roomId, "테스트");

    when(dispatcher.dispatch(request)).thenReturn(response);

    // When
    controller.handle(request);

    // Then
    verify(messaging).convertAndSend(eq("/sub/room/" + roomId), any(ActionResponse.class));
  }

  @Test
  void handleWhenDispatcherThrowsExceptionShouldNotBroadcast() {
    // Given
    ActionRequest request = createMessageRequest("ROOM_1", "테스트");

    when(dispatcher.dispatch(request))
        .thenThrow(new IllegalArgumentException("Unsupported action"));

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> controller.handle(request));
    verify(messaging, never()).convertAndSend(anyString(), any(ActionResponse.class));
  }

  @Test
  void handleJoinShouldBroadcastToRoom() {
    // Given
    ActionRequest request = createRequest("ROOM_1", "CHAT.JOIN", Map.of());
    ActionResponse response = createResponse("ROOM_1", "CHAT.JOIN", Map.of());

    when(dispatcher.dispatch(request)).thenReturn(response);

    // When
    controller.handle(request);

    // Then
    verify(messaging).convertAndSend(eq("/sub/room/ROOM_1"), any(ActionResponse.class));
  }

  @Test
  void handleLeaveShouldBroadcastToRoom() {
    // Given
    ActionRequest request = createRequest("ROOM_1", "CHAT.LEAVE", Map.of());
    ActionResponse response = createResponse("ROOM_1", "CHAT.LEAVE", Map.of());

    when(dispatcher.dispatch(request)).thenReturn(response);

    // When
    controller.handle(request);

    // Then
    verify(messaging).convertAndSend(eq("/sub/room/ROOM_1"), any(ActionResponse.class));
  }

  @Test
  void handleSystemShouldBroadcastToRoom() {
    // Given
    ActionRequest request = createRequest("ROOM_1", "CHAT.SYSTEM", Map.of("message", "공지입니다."));
    ActionResponse response = createResponse("ROOM_1", "CHAT.SYSTEM", Map.of("message", "공지입니다."));

    when(dispatcher.dispatch(request)).thenReturn(response);

    // When
    controller.handle(request);

    // Then
    verify(messaging).convertAndSend(eq("/sub/room/ROOM_1"), any(ActionResponse.class));
  }

  // Helper methods
  private ActionRequest createMessageRequest(String roomId, String message) {
    return createRequest(roomId, "CHAT.MESSAGE", Map.of("message", message));
  }

  private ActionResponse createMessageResponse(String roomId, String message) {
    return createResponse(roomId, "CHAT.MESSAGE", Map.of("message", message));
  }

  private ActionRequest createRequest(String roomId, String action, Map<String, Object> payload) {
    return new ActionRequest(
        roomId, action, new Actor("user1", "user1@example.com", "사용자1"), payload);
  }

  private ActionResponse createResponse(String roomId, String action, Map<String, Object> payload) {
    return ActionResponse.of(
        roomId, action, new Actor("user1", "user1@example.com", "사용자1"), payload);
  }
}
