package org.giglab.live.application.dto.action;

import java.time.Instant;
import org.giglab.live.application.command.ActionType;

public class ShowBannerResponse extends ActionResponse {

  private final String roomId;
  private final String action;
  private final Actor actor;
  private final Long productId;
  private final String productName;
  private final Instant sentAt;
  private final long seq;

  public ShowBannerResponse(
      String roomId,
      String action,
      Actor actor,
      Long productId,
      String productName,
      Instant sentAt,
      long seq) {
    this.roomId = roomId;
    this.action = action;
    this.actor = actor;
    this.productId = productId;
    this.productName = productName;
    this.sentAt = sentAt;
    this.seq = seq;
  }

  public static ShowBannerResponse of(ActionRequest req, Long productId, String productName) {
    return new ShowBannerResponse(
        req.roomId(),
        ActionType.PRODUCT_BANNER_ON.getKey(),
        req.actor(),
        productId,
        productName,
        Instant.now(),
        0L);
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

  public Long productId() {
    return productId;
  }

  public String productName() {
    return productName;
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
    return new ShowBannerResponse(roomId, action, actor, productId, productName, sentAt, seq);
  }
}
