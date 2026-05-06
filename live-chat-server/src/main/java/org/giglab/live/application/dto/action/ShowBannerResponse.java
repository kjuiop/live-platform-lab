package org.giglab.live.application.dto.action;

import java.time.Instant;
import org.giglab.live.application.command.ActionType;

public record ShowBannerResponse(
    String roomId,
    String action,
    Actor actor,
    Long productId,
    String productName,
    Instant sentAt,
    long seq)
    implements ActionResponse {

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
  public ActionResponse withSeq(long seq) {
    return new ShowBannerResponse(roomId, action, actor, productId, productName, sentAt, seq);
  }
}
