package org.giglab.live.application.service;

import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.domain.model.PeakViewerSnapshot;
import org.giglab.live.infrastructure.mongo.MongoPeakViewerSnapshotRepository;
import org.giglab.live.infrastructure.redis.ViewerRedisRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewerSnapshotScheduler {

  private final ViewerRedisRepository viewerRedisRepository;
  private final MongoPeakViewerSnapshotRepository snapshotRepository;

  @Scheduled(fixedDelay = 60_000)
  public void snapshot() {
    Set<String> roomIds = viewerRedisRepository.getActiveRoomIds();
    if (roomIds.isEmpty()) {
      return;
    }

    Instant now = Instant.now();
    roomIds.forEach(
        roomId -> {
          long count = viewerRedisRepository.getViewerCount(roomId);
          if (count <= 0) {
            return;
          }

          PeakViewerSnapshot snap =
              PeakViewerSnapshot.builder()
                  .roomId(roomId)
                  .viewerCount((int) count)
                  .recordedAt(now)
                  .build();

          snapshotRepository.save(snap);
          log.debug("시청자 스냅샷 저장 - roomId={}, count={}", roomId, count);
        });
  }
}
