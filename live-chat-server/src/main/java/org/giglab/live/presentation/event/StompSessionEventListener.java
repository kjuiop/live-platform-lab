package org.giglab.live.presentation.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.service.ViewerSessionService;
import org.giglab.live.presentation.StompDestination;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompSessionEventListener {

  // /sub/room/{roomId} 만 처리, /sub/room/{roomId}/host 등 하위 경로 제외

  private final ViewerSessionService viewerSessionService;

  @EventListener
  public void onSubscribe(SessionSubscribeEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String destination = accessor.getDestination();

    if (destination == null || !isRoomDestination(destination)) {
      return;
    }

    String sessionId = accessor.getSessionId();
    if (sessionId == null) {
      log.warn("sessionId 없음 - 입장 처리 스킵: destination={}", destination);
      return;
    }
    String roomId = extractRoomId(destination);

    viewerSessionService.onSubscribe(roomId, sessionId);
  }

  @EventListener
  public void onDisconnect(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    viewerSessionService.onDisconnect(sessionId);
  }

  private boolean isRoomDestination(String destination) {
    if (!destination.startsWith(StompDestination.ROOM_PREFIX)) {
      return false;
    }
    String path = destination.substring(StompDestination.ROOM_PREFIX.length());
    return !path.isEmpty() && !path.contains("/");
  }

  private String extractRoomId(String destination) {
    return destination.substring(StompDestination.ROOM_PREFIX.length());
  }
}
