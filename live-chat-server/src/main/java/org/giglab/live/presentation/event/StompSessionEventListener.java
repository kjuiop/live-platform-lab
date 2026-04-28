package org.giglab.live.presentation.event;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.domain.model.PeakViewerSnapshot;
import org.giglab.live.domain.model.ViewerSession;
import org.giglab.live.infrastructure.mongo.MongoPeakViewerSnapshotRepository;
import org.giglab.live.infrastructure.mongo.MongoViewerSessionRepository;
import org.giglab.live.infrastructure.redis.ViewerRedisRepository;
import org.giglab.live.infrastructure.redis.ViewerRedisRepository.ViewerContext;
import org.giglab.live.infrastructure.redis.pubsub.RoomBroadcastPublisher;
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
  private static final String ROOM_DESTINATION_PREFIX = "/sub/room/";

  private final ViewerRedisRepository viewerRedisRepository;
  private final MongoViewerSessionRepository viewerSessionRepository;
  private final MongoPeakViewerSnapshotRepository peakViewerSnapshotRepository;
  private final RoomBroadcastPublisher publisher;

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

    viewerRedisRepository.addViewer(roomId, sessionId);
    log.debug("시청자 입장 - sessionId={}, roomId={}", sessionId, roomId);
    saveSnapshot(roomId);
    broadcastViewerCount(roomId);
  }

  @EventListener
  public void onDisconnect(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();

    Optional<ViewerContext> findViewer = viewerRedisRepository.getAndRemoveViewer(sessionId);
    if (findViewer.isEmpty()) {
      log.debug("세션 정보 없음 - sessionId={}", sessionId);
      return;
    }

    ViewerContext ctx = findViewer.get();
    Instant leaveAt = Instant.now();
    long durationSeconds = Math.max(0, Duration.between(ctx.joinAt(), leaveAt).getSeconds());

    // 시청 기록 저장
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
    broadcastViewerCount(ctx.roomId());
  }

  private void saveSnapshot(String roomId) {
    long count = viewerRedisRepository.getViewerCount(roomId);
    if (count <= 0) {
      return;
    }
    peakViewerSnapshotRepository.save(
        PeakViewerSnapshot.builder()
            .roomId(roomId)
            .viewerCount((int) count)
            .recordedAt(Instant.now())
            .build());
  }

  private void broadcastViewerCount(String roomId) {
    long count = viewerRedisRepository.getViewerCount(roomId);
    Map<String, Object> message =
        Map.of(
            "action", ActionType.VIEWER_COUNT.getKey(),
            "roomId", roomId,
            "payload", Map.of("count", count));
    publisher.publish(roomId, message);
    log.debug("시청자 수 브로드캐스트 - roomId={}, count={}", roomId, count);
  }

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
