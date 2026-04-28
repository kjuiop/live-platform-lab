package org.giglab.live.presentation.api.v1.controller.websocket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.application.RoomActionFacade;
import org.giglab.live.application.dto.action.ActionRequest;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomActionController {

  private static final String DESTINATION = "/room.action";

  private final RoomActionFacade roomActionFacade;

  @MessageMapping(DESTINATION)
  public void handle(@Valid ActionRequest req, SimpMessageHeaderAccessor headerAccessor) {
    roomActionFacade.handle(req, headerAccessor.getSessionId());
  }

  @MessageExceptionHandler
  public void handleException(Exception e) {
    log.warn("STOMP handler error: {}", e.getMessage());
  }
}
