package org.giglab.live.commerce.core.product.application.dto.ai;

import org.giglab.live.commerce.core.product.domain.entity.ProductFaqSample;

public record ProductFaqSampleResult(Long id, String question, String answer) {
  public static ProductFaqSampleResult from(ProductFaqSample sample) {
    return new ProductFaqSampleResult(sample.getId(), sample.getQuestion(), sample.getAnswer());
  }
}
