package org.giglab.live.application.service;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.port.persistence.ViewerSessionPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewerSessionService {

  private final ViewerSessionPort viewerSessionPort;

  public void saveUserIdIfJoin(ActionRequest req, String sessionId) {
    if (!ActionType.CHAT_JOIN.getKey().equals(req.action())) {
      return;
    }
    if (sessionId == null || req.actor() == null || req.actor().userId() == null) {
      return;
    }
    viewerSessionPort.saveUserId(sessionId, req.actor().userId());
  }
}
