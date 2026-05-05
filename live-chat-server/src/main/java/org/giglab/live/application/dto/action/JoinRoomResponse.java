package org.giglab.live.application.dto.action;

import java.time.Instant;
import org.giglab.live.application.command.ActionType;

public class JoinRoomResponse extends ActionResponse {

  private final String roomId;
  private final String action;
  private final Actor actor;
  private final ActiveBanner activeBanner;
  private final Instant sentAt;
  private final long seq;

  public JoinRoomResponse(
      String roomId,
      String action,
      Actor actor,
      ActiveBanner activeBanner,
      Instant sentAt,
      long seq) {
    this.roomId = roomId;
    this.action = action;
    this.actor = actor;
    this.activeBanner = activeBanner;
    this.sentAt = sentAt;
    this.seq = seq;
  }

  public static JoinRoomResponse of(ActionRequest req, ActiveBanner activeBanner) {
    return new JoinRoomResponse(
        req.roomId(), ActionType.CHAT_JOIN.getKey(), req.actor(), activeBanner, Instant.now(), 0L);
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

  public ActiveBanner activeBanner() {
    return activeBanner;
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
    return new JoinRoomResponse(roomId, action, actor, activeBanner, sentAt, seq);
  }
}
