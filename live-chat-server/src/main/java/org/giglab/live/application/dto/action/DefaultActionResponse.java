package org.giglab.live.application.dto.action;

import java.time.Instant;
import java.util.Map;

public record DefaultActionResponse(
    String roomId,
    String action,
    Actor actor,
    Map<String, Object> payload,
    Instant sentAt,
    long seq)
    implements ActionResponse {

  public static DefaultActionResponse of(
      String roomId, String action, Actor actor, Map<String, Object> payload) {
    return new DefaultActionResponse(roomId, action, actor, payload, Instant.now(), 0L);
  }

  @Override
  public ActionResponse withSeq(long seq) {
    return new DefaultActionResponse(roomId, action, actor, payload, sentAt, seq);
  }
}
