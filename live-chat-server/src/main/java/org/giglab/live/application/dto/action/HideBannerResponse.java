package org.giglab.live.application.dto.action;

import java.time.Instant;
import org.giglab.live.application.command.ActionType;

public class HideBannerResponse extends ActionResponse {

  private final String roomId;
  private final String action;
  private final Actor actor;
  private final Instant sentAt;
  private final long seq;

  public HideBannerResponse(String roomId, String action, Actor actor, Instant sentAt, long seq) {
    this.roomId = roomId;
    this.action = action;
    this.actor = actor;
    this.sentAt = sentAt;
    this.seq = seq;
  }

  public static HideBannerResponse of(ActionRequest req) {
    return new HideBannerResponse(
        req.roomId(), ActionType.PRODUCT_BANNER_OFF.getKey(), req.actor(), Instant.now(), 0L);
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
  public Instant sentAt() {
    return sentAt;
  }

  @Override
  public long seq() {
    return seq;
  }

  @Override
  public ActionResponse withSeq(long seq) {
    return new HideBannerResponse(roomId, action, actor, sentAt, seq);
  }
}
