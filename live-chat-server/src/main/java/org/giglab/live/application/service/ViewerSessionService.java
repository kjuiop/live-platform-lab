package org.giglab.live.application.service;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.port.persistence.BroadcastPort;
import org.giglab.live.application.port.persistence.ViewerContext;
import org.giglab.live.application.port.persistence.ViewerSessionPort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewerSessionService {

  private final ViewerSessionPort viewerSessionPort;
  private final BroadcastPort broadcastPort;

  public void saveUserIdIfJoin(ActionRequest req, String sessionId) {
    if (!ActionType.CHAT_JOIN.getKey().equals(req.action())) {
      return;
    }
    if (sessionId == null || req.actor() == null || req.actor().userId() == null) {
      return;
    }
    viewerSessionPort.saveUserId(sessionId, req.actor().userId());
  }

  public void onSubscribe(String roomId, String sessionId) {
    viewerSessionPort.addViewer(roomId, sessionId);
    log.debug("시청자 입장 - sessionId={}, roomId={}", sessionId, roomId);
    broadcastViewerCount(roomId);
  }

  public void onDisconnect(String sessionId) {
    Optional<ViewerContext> findContext = viewerSessionPort.getAndRemoveViewer(sessionId);
    if (findContext.isEmpty()) {
      log.debug("세션 정보 없음 - TTL 만료 또는 중복 disconnect: sessionId={}", sessionId);
      return;
    }
    ViewerContext ctx = findContext.get();
    try {
      viewerSessionPort.saveSession(ctx);
    } catch (Exception e) {
      log.warn("시청 기록 저장 실패 - sessionId={}, roomId={}", sessionId, ctx.roomId(), e);
    }
    broadcastViewerCount(ctx.roomId());
    log.debug("시청자 퇴장 - sessionId={}, roomId={}", sessionId, ctx.roomId());
  }

  private void broadcastViewerCount(String roomId) {
    long count = viewerSessionPort.getViewerCount(roomId);
    Map<String, Object> message =
        Map.of(
            "action", ActionType.VIEWER_COUNT.getKey(),
            "roomId", roomId,
            "payload", Map.of("count", count));
    broadcastPort.publish(roomId, message);
    log.debug("시청자 수 브로드캐스트 - roomId={}, count={}", roomId, count);
  }
}
