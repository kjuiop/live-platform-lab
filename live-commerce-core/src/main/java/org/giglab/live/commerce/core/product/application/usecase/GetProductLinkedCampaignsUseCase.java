package org.giglab.live.commerce.core.product.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.giglab.live.commerce.core.product.application.dto.GetProductLinkedCampaignsResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductLinkedCampaignsUseCase {

  private final CampaignQueryPort campaignQueryPort;

  public GetProductLinkedCampaignsResult execute(Long productId) {
    return new GetProductLinkedCampaignsResult(campaignQueryPort.findByProductId(productId));
  }
}
