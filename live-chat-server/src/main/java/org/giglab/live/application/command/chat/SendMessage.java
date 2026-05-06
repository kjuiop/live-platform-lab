package org.giglab.live.application.command.chat;

import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.DefaultActionResponse;
import org.giglab.live.domain.exception.ChatDomainException;
import org.giglab.live.domain.exception.ChatErrorCode;
import org.springframework.stereotype.Service;

@Service
public class SendMessage extends AbstractActionHandler<ActionRequest, ActionResponse> {

  @Override
  public ActionType action() {
    return ActionType.CHAT_MESSAGE;
  }

  @Override
  protected void validate(ActionRequest req) {
    requireString(
        req.payload(), "message", () -> new ChatDomainException(ChatErrorCode.EMPTY_MESSAGE));
  }

  @Override
  protected ActionResponse process(ActionRequest req) {
    return DefaultActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
