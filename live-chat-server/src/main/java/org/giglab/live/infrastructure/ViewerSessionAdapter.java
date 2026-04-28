package org.giglab.live.infrastructure;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.port.persistence.ViewerContext;
import org.giglab.live.application.port.persistence.ViewerSessionPort;
import org.giglab.live.domain.model.PeakViewerSnapshot;
import org.giglab.live.domain.model.ViewerSession;
import org.giglab.live.infrastructure.mongo.MongoPeakViewerSnapshotRepository;
import org.giglab.live.infrastructure.mongo.MongoViewerSessionRepository;
import org.giglab.live.infrastructure.redis.ViewerRedisRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewerSessionAdapter implements ViewerSessionPort {

  private final ViewerRedisRepository viewerRedisRepository;
  private final MongoViewerSessionRepository viewerSessionRepository;
  private final MongoPeakViewerSnapshotRepository peakViewerSnapshotRepository;

  @Override
  public void saveUserId(String sessionId, String userId) {
    viewerRedisRepository.saveUserId(sessionId, userId);
  }

  @Override
  public void addViewer(String roomId, String sessionId) {
    viewerRedisRepository.addViewer(roomId, sessionId);
    long count = viewerRedisRepository.getViewerCount(roomId);
    if (count > 0) {
      peakViewerSnapshotRepository.save(
          PeakViewerSnapshot.builder()
              .roomId(roomId)
              .viewerCount((int) count)
              .recordedAt(Instant.now())
              .build());
    }
  }

  @Override
  public Optional<ViewerContext> getAndRemoveViewer(String sessionId) {
    return viewerRedisRepository
        .getAndRemoveViewer(sessionId)
        .map(rc -> new ViewerContext(rc.sessionId(), rc.roomId(), rc.userId(), rc.joinAt()));
  }

  @Override
  public long getViewerCount(String roomId) {
    return viewerRedisRepository.getViewerCount(roomId);
  }

  @Override
  public void saveSession(ViewerContext ctx) {
    Instant leaveAt = Instant.now();
    long durationSeconds = Math.max(0, Duration.between(ctx.joinAt(), leaveAt).getSeconds());

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
        ctx.sessionId(),
        ctx.roomId(),
        durationSeconds);
  }
}
