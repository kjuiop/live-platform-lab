package org.giglab.live.application.command.chat;

import org.giglab.live.application.command.ActionHandler;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.springframework.stereotype.Service;

@Service
public class SendMessage implements ActionHandler<ActionRequest, ActionResponse> {

  @Override
  public String action() {
    return "CHAT.MESSAGE";
  }

  @Override
  public ActionResponse execute(ActionRequest req) {
    Object msg = req.payload() == null ? null : req.payload().get("message");
    if (!(msg instanceof String s) || s.isBlank()) {
      throw new IllegalArgumentException("payload.message is required");
    }
    return ActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
