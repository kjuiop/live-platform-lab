package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ai.GetProductFaqSamplesResult;
import org.giglab.live.commerce.core.product.application.dto.ai.ProductFaqSampleResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductFaqSamplePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductFaqSamplesUseCase {

  private final ProductFaqSamplePort productFaqSamplePort;

  public GetProductFaqSamplesResult execute(Long productId) {
    List<ProductFaqSampleResult> items =
        productFaqSamplePort.findByProductId(productId).stream()
            .map(ProductFaqSampleResult::from)
            .toList();
    return new GetProductFaqSamplesResult(productId, items);
  }
}
