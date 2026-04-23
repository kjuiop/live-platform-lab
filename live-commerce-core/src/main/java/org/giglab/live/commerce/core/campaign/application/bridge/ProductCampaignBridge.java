package org.giglab.live.commerce.core.campaign.application.bridge;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.giglab.live.commerce.core.product.application.port.bridge.ProductCampaignAppPort;
import org.giglab.live.commerce.core.shared.ProductLinkedCampaignDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductCampaignBridge implements ProductCampaignAppPort {

  private final CampaignQueryPort campaignQueryPort;

  @Override
  public List<ProductLinkedCampaignDto> getLinkedCampaignList(Long productId) {
    return campaignQueryPort.findByProductId(productId);
  }
}
