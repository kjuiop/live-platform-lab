package org.giglab.live.presentation.api.v1.controller.websocket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.Actor;
import org.giglab.live.application.facade.RoomActionFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@ExtendWith(MockitoExtension.class)
class RoomActionControllerTest {

  @Mock private RoomActionFacade roomActionFacade;

  private RoomActionController controller;
  private SimpMessageHeaderAccessor headerAccessor;

  @BeforeEach
  void setUp() {
    controller = new RoomActionController(roomActionFacade);
    headerAccessor = Mockito.mock(SimpMessageHeaderAccessor.class);
    Mockito.doReturn("test-session-id").when(headerAccessor).getSessionId();
  }

  @Test
  void handleShouldDelegateToRoomActionFacade() {
    // Given
    ActionRequest request = createRequest("ROOM_1", "CHAT.MESSAGE", Map.of("message", "안녕하세요"));

    // When
    controller.handle(request, headerAccessor);

    // Then
    verify(roomActionFacade).handle(request, "test-session-id");
  }

  @Test
  void handleWhenServiceThrowsExceptionShouldPropagateToExceptionHandler() {
    // Given
    ActionRequest request = createRequest("ROOM_1", "CHAT.MESSAGE", Map.of());
    doThrow(new IllegalArgumentException("Unsupported action"))
        .when(roomActionFacade)
        .handle(any(ActionRequest.class), anyString());

    // When & Then
    // 실제 Spring STOMP 인프라에서는 @MessageExceptionHandler 가 이를 가로채 세션을 유지함
    assertThrows(IllegalArgumentException.class, () -> controller.handle(request, headerAccessor));
  }

  @Test
  void handleExceptionShouldNotDelegate() {
    // Given
    IllegalArgumentException exception =
        new IllegalArgumentException("Unsupported action: UNKNOWN");

    // When
    controller.handleException(exception);

    // Then
    verify(roomActionFacade, never()).handle(any(ActionRequest.class), anyString());
  }

  private ActionRequest createRequest(String roomId, String action, Map<String, Object> payload) {
    return new ActionRequest(
        roomId, action, new Actor("user1", "user1@example.com", "사용자1"), payload);
  }
}
