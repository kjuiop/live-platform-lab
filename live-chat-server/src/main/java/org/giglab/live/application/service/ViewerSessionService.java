package org.giglab.live.application.service;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.infrastructure.redis.ViewerRedisRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewerSessionService {

  private final ViewerRedisRepository viewerRedisRepository;

  public void saveUserIdIfJoin(ActionRequest req, String sessionId) {
    if (!ActionType.CHAT_JOIN.getKey().equals(req.action())) {
      return;
    }
    if (sessionId == null || req.actor() == null || req.actor().userId() == null) {
      return;
    }
    viewerRedisRepository.saveUserId(sessionId, req.actor().userId());
  }
}
