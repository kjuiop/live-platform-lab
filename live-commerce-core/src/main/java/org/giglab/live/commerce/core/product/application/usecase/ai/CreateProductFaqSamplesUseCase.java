package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.core.product.application.dto.ai.FaqSampleItem;
import org.giglab.live.commerce.core.product.application.dto.ai.GenerateProductFaqSamplesResult;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductFaqSamplePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductFaqSample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductFaqSamplesUseCase {

  private final ProductFaqSamplePort productFaqSamplePort;

  @Transactional
  public GenerateProductFaqSamplesResult execute(
      Long productId, GenerateProductFaqSamplesResult result) {
    List<FaqSampleItem> samples = result.samples();
    if (samples.isEmpty()) {
      return result;
    }

    List<ProductFaqSample> entities =
        samples.stream()
            .map(item -> ProductFaqSample.create(productId, item.question(), item.answer()))
            .toList();
    productFaqSamplePort.saveAll(entities);

    log.info("사전 Q&A 저장 완료 - productId={}, count={}", productId, entities.size());
    return result;
  }
}
