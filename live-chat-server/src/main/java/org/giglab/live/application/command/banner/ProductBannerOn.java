package org.giglab.live.application.command.banner;

import lombok.RequiredArgsConstructor;
import org.giglab.live.application.command.AbstractActionHandler;
import org.giglab.live.application.command.ActionType;
import org.giglab.live.application.dto.action.ActionRequest;
import org.giglab.live.application.dto.action.ShowBannerResponse;
import org.giglab.live.application.port.persistence.BannerStatePort;
import org.giglab.live.domain.exception.BannerDomainException;
import org.giglab.live.domain.exception.BannerErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductBannerOn extends AbstractActionHandler<ActionRequest, ShowBannerResponse> {

  private final BannerStatePort bannerStatePort;

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
    requireString(
        req.payload(),
        "productName",
        () -> new BannerDomainException(BannerErrorCode.INVALID_PRODUCT_NAME));
  }

  @Override
  protected ShowBannerResponse process(ActionRequest req) {
    Long productId = ((Number) req.payload().get("productId")).longValue();
    String productName = (String) req.payload().get("productName");
    ShowBannerResponse response = ShowBannerResponse.of(req, productId, productName);
    bannerStatePort.save(req.roomId(), response);
    return response;
  }
}
