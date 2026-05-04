package org.giglab.live.application.command;

import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;

public interface ActionHandler<RequestT extends ActionRequest, ResponseT extends ActionResponse> {
  ActionType action();

  ResponseT execute(RequestT request);
}
