package org.giglab.live.application.command.chat;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActiveBanner;
import org.giglab.live.application.dto.action.JoinRoomResponse;
import org.giglab.live.application.port.persistence.BannerStatePort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinRoom extends AbstractActionHandler<ActionRequest, JoinRoomResponse> {

  private final BannerStatePort bannerStatePort;

  @Override
  public ActionType action() {
    return ActionType.CHAT_JOIN;
  }

  @Override
  protected JoinRoomResponse process(ActionRequest req) {
    ActiveBanner activeBanner = bannerStatePort.getActiveBanner(req.roomId()).orElse(null);
    return JoinRoomResponse.of(req, activeBanner);
  }
}
