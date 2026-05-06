package org.giglab.live.application.command.chat;

import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.DefaultActionResponse;
import org.springframework.stereotype.Service;

@Service
public class LeaveRoom extends AbstractActionHandler<ActionRequest, ActionResponse> {

  @Override
  public ActionType action() {
    return ActionType.CHAT_LEAVE;
  }

  @Override
  protected ActionResponse process(ActionRequest req) {
    return DefaultActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
