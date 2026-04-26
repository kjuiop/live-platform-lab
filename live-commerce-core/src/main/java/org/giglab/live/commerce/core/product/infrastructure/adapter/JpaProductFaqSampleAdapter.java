package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductFaqSamplePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductFaqSample;
import org.giglab.live.commerce.core.product.infrastructure.persistence.ProductFaqSampleRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductFaqSampleAdapter implements ProductFaqSamplePort {

  private final ProductFaqSampleRepository repository;

  @Override
  public List<ProductFaqSample> saveAll(List<ProductFaqSample> samples) {
    return repository.saveAll(samples);
  }

  @Override
  public List<ProductFaqSample> findByProductId(Long productId) {
    return repository.findByProductId(productId);
  }
}
