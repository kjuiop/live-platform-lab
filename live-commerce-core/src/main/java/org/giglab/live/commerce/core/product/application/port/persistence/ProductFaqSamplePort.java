package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.domain.entity.ProductFaqSample;

public interface ProductFaqSamplePort {
  List<ProductFaqSample> saveAll(List<ProductFaqSample> samples);

  List<ProductFaqSample> findByProductId(Long productId);
}
