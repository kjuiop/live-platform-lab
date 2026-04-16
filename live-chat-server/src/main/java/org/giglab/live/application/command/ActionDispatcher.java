package org.giglab.live.application.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.springframework.stereotype.Service;

@Service
public class ActionDispatcher {

  // action key -> handler 매핑
  // map 으로 변환해서 주입한 이유는 dispatch 시 O(1)로 handler 를 찾기 위해서이다.
  private final Map<String, ActionHandler<ActionRequest, ActionResponse>> handlers;

  // JoinRoom, LeaveRoom, SendMessage 등 ActionHandler 구현체들을 주입받아 action key -> handler 매핑을 생성
  // 따라서 list 로 받는다.
  public ActionDispatcher(List<ActionHandler<ActionRequest, ActionResponse>> list) {
    this.handlers =
        list.stream()
            .collect(
                Collectors.toMap(
                    h -> h.action().getKey(),
                    h -> h,
                    (a, b) -> {
                      throw new IllegalStateException(
                          "Duplicate handler for action: " + a.action().getKey());
                    }));
  }

  public ActionResponse dispatch(ActionRequest req) {
    ActionHandler<ActionRequest, ActionResponse> handler = handlers.get(req.action());
    if (handler == null) {
      throw new IllegalArgumentException("Unsupported action: " + req.action());
    }
    return handler.execute(req);
  }
}
