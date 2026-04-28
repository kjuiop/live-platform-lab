package org.giglab.live.application.dto.action;

import java.time.Instant;
import java.util.Map;

public record ActionResponse(
    String roomId, String action, Actor actor, Map<String, Object> payload, Instant sentAt) {

  public static ActionResponse of(
      String roomId, String action, Actor actor, Map<String, Object> payload) {
    return new ActionResponse(roomId, action, actor, payload, Instant.now());
  }
}
