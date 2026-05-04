package org.giglab.live.application.dto.action;

import java.time.Instant;
import java.util.Map;

public class DefaultActionResponse extends ActionResponse {

  private final String roomId;
  private final String action;
  private final Actor actor;
  private final Map<String, Object> payload;
  private final Instant sentAt;
  private final long seq;

  public DefaultActionResponse(
      String roomId,
      String action,
      Actor actor,
      Map<String, Object> payload,
      Instant sentAt,
      long seq) {
    this.roomId = roomId;
    this.action = action;
    this.actor = actor;
    this.payload = payload;
    this.sentAt = sentAt;
    this.seq = seq;
  }

  public static DefaultActionResponse of(
      String roomId, String action, Actor actor, Map<String, Object> payload) {
    return new DefaultActionResponse(roomId, action, actor, payload, Instant.now(), 0L);
  }

  @Override
  public String roomId() {
    return roomId;
  }

  @Override
  public String action() {
    return action;
  }

  @Override
  public Actor actor() {
    return actor;
  }

  @Override
  public Map<String, Object> payload() {
    return payload;
  }

  @Override
  public Instant sentAt() {
    return sentAt;
  }

  @Override
  public long seq() {
    return seq;
  }

  @Override
  public ActionResponse withSeq(long seq) {
    return new DefaultActionResponse(roomId, action, actor, payload, sentAt, seq);
  }
}
