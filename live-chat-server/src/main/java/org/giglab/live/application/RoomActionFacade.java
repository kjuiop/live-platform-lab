package org.giglab.live.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionDispatcher;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.port.messaging.BroadcastPort;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.application.service.ViewerSessionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomActionFacade {

  private final ActionDispatcher dispatcher;
  private final BroadcastPort broadcastPort;
  private final ChatMessageService chatMessageService;
  private final ViewerSessionService viewerSessionService;

  public void handle(ActionRequest req, String sessionId) {
    ActionResponse res = dispatcher.dispatch(req);
    broadcastPort.publish(req.roomId(), res);
    chatMessageService.saveIfNeeded(res);
    try {
      viewerSessionService.saveUserIdIfJoin(req, sessionId);
    } catch (Exception e) {
      log.warn("시청자 userId 저장 실패 - sessionId={}", sessionId, e);
    }
  }
}
