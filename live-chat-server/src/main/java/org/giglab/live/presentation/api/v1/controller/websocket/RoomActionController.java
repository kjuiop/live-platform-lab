package org.giglab.live.presentation.api.v1.controller.websocket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.command.ActionDispatcher;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.service.ChatMessageService;
import org.giglab.live.application.service.ViewerSessionService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomActionController {
  private static final String DESTINATION = "/room.action";
  private static final String SUBSCRIBE_PREFIX = "/sub/room/";

  private final ActionDispatcher dispatcher;
  private final SimpMessageSendingOperations operations;
  private final ChatMessageService chatMessageService;
  private final ViewerSessionService viewerSessionService;

  @MessageMapping(DESTINATION)
  public void handle(@Valid ActionRequest req, SimpMessageHeaderAccessor headerAccessor) {
    ActionResponse res = dispatcher.dispatch(req);
    operations.convertAndSend(SUBSCRIBE_PREFIX + req.roomId(), res);
    chatMessageService.saveIfNeeded(res);
    viewerSessionService.saveUserIdIfJoin(req, headerAccessor.getSessionId());
  }

  @MessageExceptionHandler
  public void handleException(Exception e) {
    log.warn("STOMP handler error: {}", e.getMessage());
  }
}
