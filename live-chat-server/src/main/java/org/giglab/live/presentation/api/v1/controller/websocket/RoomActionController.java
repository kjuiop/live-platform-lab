package org.giglab.live.presentation.api.v1.controller.websocket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionDispatcher;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.application.service.ViewerSessionService;
import org.giglab.live.infrastructure.redis.pubsub.RoomBroadcastPublisher;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomActionController {
  private static final String DESTINATION = "/room.action";

  private final ActionDispatcher dispatcher;
  private final RoomBroadcastPublisher publisher;
  private final ChatMessageService chatMessageService;
  private final ViewerSessionService viewerSessionService;

  @MessageMapping(DESTINATION)
  public void handle(@Valid ActionRequest req, SimpMessageHeaderAccessor headerAccessor) {
    ActionResponse res = dispatcher.dispatch(req);
    publisher.publish(req.roomId(), res);
    chatMessageService.saveIfNeeded(res);
    try {
      viewerSessionService.saveUserIdIfJoin(req, headerAccessor.getSessionId());
    } catch (Exception e) {
      log.warn("시청자 userId 저장 실패 (무시) - sessionId={}", headerAccessor.getSessionId(), e);
    }
  }

  @MessageExceptionHandler
  public void handleException(Exception e) {
    log.warn("STOMP handler error: {}", e.getMessage());
  }
}
