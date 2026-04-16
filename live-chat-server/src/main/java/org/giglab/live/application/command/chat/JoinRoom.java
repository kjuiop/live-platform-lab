package org.giglab.live.application.command.chat;

import org.giglab.live.application.command.ActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.springframework.stereotype.Service;

@Service
public class JoinRoom implements ActionHandler<ActionRequest, ActionResponse> {

  @Override
  public ActionType action() {
    return ActionType.CHAT_JOIN;
  }

  @Override
  public ActionResponse execute(ActionRequest req) {
    if (req.actor() == null) {
      throw new IllegalArgumentException("actor is required for CHAT.JOIN");
    }
    return ActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
