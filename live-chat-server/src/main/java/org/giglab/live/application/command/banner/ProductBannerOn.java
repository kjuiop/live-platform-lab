package org.giglab.live.application.command.banner;

import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ActionResponse;
import org.giglab.live.application.dto.action.DefaultActionResponse;
import org.giglab.live.domain.exception.BannerDomainException;
import org.giglab.live.domain.exception.BannerErrorCode;
import org.springframework.stereotype.Service;

@Service
public class ProductBannerOn extends AbstractActionHandler<ActionRequest, ActionResponse> {

  @Override
  public ActionType action() {
    return ActionType.PRODUCT_BANNER_ON;
  }

  @Override
  protected void validate(ActionRequest req) {
    requirePositiveLong(
        req.payload(),
        "productId",
        () -> new BannerDomainException(BannerErrorCode.INVALID_PRODUCT_ID));
  }

  @Override
  protected ActionResponse process(ActionRequest req) {
    return DefaultActionResponse.of(req.roomId(), req.action(), req.actor(), req.payload());
  }
}
