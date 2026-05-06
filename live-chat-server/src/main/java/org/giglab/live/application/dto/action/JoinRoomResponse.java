package org.giglab.live.application.dto.action;

import java.time.Instant;
import org.giglab.live.application.command.ActionType;

public record JoinRoomResponse(
    String roomId, String action, Actor actor, ActiveBanner activeBanner, Instant sentAt, long seq)
    implements ActionResponse {

  public static JoinRoomResponse of(ActionRequest req, ActiveBanner activeBanner) {
    return new JoinRoomResponse(
        req.roomId(), ActionType.CHAT_JOIN.getKey(), req.actor(), activeBanner, Instant.now(), 0L);
  }

  @Override
  public ActionResponse withSeq(long seq) {
    return new JoinRoomResponse(roomId, action, actor, activeBanner, sentAt, seq);
  }
}
