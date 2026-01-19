package org.giglab.live.application.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.springframework.stereotype.Service;

@Service
public class ActionDispatcher {

  private final Map<String, ActionHandler<ActionRequest, ActionResponse>> handlers;

  public ActionDispatcher(List<ActionHandler<ActionRequest, ActionResponse>> list) {
    this.handlers = list.stream().collect(Collectors.toMap(ActionHandler::action, h -> h));
  }

  public ActionResponse dispatch(ActionRequest req) {
    ActionHandler<ActionRequest, ActionResponse> handler = handlers.get(req.action());
    if (handler == null) {
      throw new IllegalArgumentException("Unsupported action: " + req.action());
    }
    return handler.execute(req);
  }
}
