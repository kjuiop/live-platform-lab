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
    ActionResponse resWithSeq = chatMessageService.assignSeq(res);
    broadcastPort.publish(req.roomId(), resWithSeq);
    chatMessageService.saveIfNeeded(resWithSeq);
    viewerSessionService.saveUserIdIfJoin(req, sessionId);
  }
}
