package org.giglab.live.commerce.core.product.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.GetProductLinkedCampaignsResult;
import org.giglab.live.commerce.core.product.application.port.bridge.ProductCampaignAppPort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductQueryPort;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductLinkedCampaignsUseCase {

  private final ProductQueryPort productQueryPort;
  private final ProductCampaignAppPort productCampaignAppPort;

  public GetProductLinkedCampaignsResult execute(Long productId) {
    if (productQueryPort.findById(productId).isEmpty()) {
      throw new ProductDomainException(
          ProductErrorCode.PRODUCT_NOT_FOUND, String.format("존재하지 않는 상품 %d 입니다.", productId));
    }
    return new GetProductLinkedCampaignsResult(
        productCampaignAppPort.getLinkedCampaignList(productId));
  }
}
