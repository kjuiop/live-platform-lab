package org.giglab.live.commerce.core.product.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.GetProductLinkedCampaignsResult;
import org.giglab.live.commerce.core.product.application.port.bridge.ProductCampaignAppPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductLinkedCampaignsUseCase {

  private final ProductCampaignAppPort productCampaignAppPort;

  public GetProductLinkedCampaignsResult execute(Long productId) {
    return new GetProductLinkedCampaignsResult(
        productCampaignAppPort.getLinkedCampaignList(productId));
  }
}
