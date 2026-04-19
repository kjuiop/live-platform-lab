package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductQueryPort;
import org.giglab.live.commerce.core.product.infrastructure.persistence.ProductQueryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductQueryAdapter implements ProductQueryPort {

  private final ProductQueryRepository queryRepository;

  @Override
  public List<ProductSummary> findList(ProductListQuery query) {
    return queryRepository.findList(query);
  }
}
