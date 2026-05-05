package org.giglab.live.application.command.banner;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.HideBannerResponse;
import org.giglab.live.application.port.persistence.BannerStatePort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductBannerOff extends AbstractActionHandler<ActionRequest, HideBannerResponse> {

  private final BannerStatePort bannerStatePort;

  @Override
  public ActionType action() {
    return ActionType.PRODUCT_BANNER_OFF;
  }

  @Override
  protected HideBannerResponse process(ActionRequest req) {
    bannerStatePort.clear(req.roomId());
    return HideBannerResponse.of(req);
  }
}
