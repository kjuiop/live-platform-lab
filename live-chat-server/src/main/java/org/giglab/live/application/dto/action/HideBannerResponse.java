package org.giglab.live.application.dto.action;

import java.time.Instant;
import org.giglab.live.application.command.ActionType;

public record HideBannerResponse(
    String roomId, String action, Actor actor, Instant sentAt, long seq) implements ActionResponse {

  public static HideBannerResponse of(ActionRequest req) {
    return new HideBannerResponse(
        req.roomId(), ActionType.PRODUCT_BANNER_OFF.getKey(), req.actor(), Instant.now(), 0L);
  }

  @Override
  public ActionResponse withSeq(long seq) {
    return new HideBannerResponse(roomId, action, actor, sentAt, seq);
  }
}
