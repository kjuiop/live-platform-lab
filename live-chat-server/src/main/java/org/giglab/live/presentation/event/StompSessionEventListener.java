package org.giglab.live.presentation.event;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.domain.model.ViewerSession;
import org.giglab.live.infrastructure.mongo.MongoViewerSessionRepository;
import org.giglab.live.infrastructure.redis.ViewerRedisRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompSessionEventListener {

  private static final String ROOM_DESTINATION_PREFIX = "/sub/room/";

  private final ViewerRedisRepository viewerRedisRepository;
  private final MongoViewerSessionRepository viewerSessionRepository;

  @EventListener
  public void onSubscribe(SessionSubscribeEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    String destination = accessor.getDestination();

    if (destination == null || !isRoomDestination(destination)) {
      return;
    }

    String sessionId = accessor.getSessionId();
    String roomId = extractRoomId(destination);

    viewerRedisRepository.addViewer(roomId, sessionId);
    log.debug("시청자 입장 - sessionId={}, roomId={}", sessionId, roomId);
  }

  @EventListener
  public void onDisconnect(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();

    viewerRedisRepository
        .getAndRemoveViewer(sessionId)
        .ifPresent(
            ctx -> {
              Instant leaveAt = Instant.now();
              long durationSeconds = Duration.between(ctx.joinAt(), leaveAt).getSeconds();

              ViewerSession session =
                  ViewerSession.builder()
                      .sessionId(ctx.sessionId())
                      .roomId(ctx.roomId())
                      .userId(ctx.userId())
                      .joinAt(ctx.joinAt())
                      .leaveAt(leaveAt)
                      .durationSeconds(durationSeconds)
                      .build();

              viewerSessionRepository.save(session);
              log.debug(
                  "시청자 퇴장 저장 - sessionId={}, roomId={}, duration={}s",
                  sessionId,
                  ctx.roomId(),
                  durationSeconds);
            });
  }

  // /sub/room/{roomId} 만 처리, /sub/room/{roomId}/host 등 하위 경로 제외
  private boolean isRoomDestination(String destination) {
    if (!destination.startsWith(ROOM_DESTINATION_PREFIX)) {
      return false;
    }
    String path = destination.substring(ROOM_DESTINATION_PREFIX.length());
    return !path.isEmpty() && !path.contains("/");
  }

  private String extractRoomId(String destination) {
    return destination.substring(ROOM_DESTINATION_PREFIX.length());
  }
}
