package org.giglab.live.commerce.core.product.infrastructure.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.domain.entity.ProductFaqSample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductFaqSampleRepository extends JpaRepository<ProductFaqSample, Long> {
  List<ProductFaqSample> findByProductId(Long productId);
}
