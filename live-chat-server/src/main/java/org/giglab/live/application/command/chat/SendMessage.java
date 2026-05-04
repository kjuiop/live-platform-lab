package org.giglab.live.application.command.chat;

import org.giglab.live.application.command.ActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.DefaultActionResponse;
import org.giglab.live.domain.exception.ChatDomainException;
import org.giglab.live.domain.exception.ChatErrorCode;
import org.springframework.stereotype.Service;

@Service
public class SendMessage implements ActionHandler<ActionRequest, ActionResponse> {

  @Override
  public ActionType action() {
    return ActionType.CHAT_MESSAGE;
  }

  @Override
  public ActionResponse execute(ActionRequest req) {
    Object msg = req.payload() == null ? null : req.payload().get("message");
    if (!(msg instanceof String s) || s.isBlank()) {
      throw new ChatDomainException(ChatErrorCode.EMPTY_MESSAGE);
    }
    return DefaultActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
