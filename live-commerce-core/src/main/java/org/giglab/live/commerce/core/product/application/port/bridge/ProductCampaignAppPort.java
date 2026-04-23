package org.giglab.live.commerce.core.product.application.port.bridge;

import java.util.List;
import org.giglab.live.commerce.core.shared.ProductLinkedCampaignDto;

public interface ProductCampaignAppPort {
  List<ProductLinkedCampaignDto> getLinkedCampaignList(Long productId);
}
