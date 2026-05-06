package org.giglab.live.commerce.core.product.infrastructure.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductPageQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductQueryPort;
import org.giglab.live.commerce.core.product.domain.entity.Product;
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

  @Override
  public List<ProductSummary> findPage(ProductPageQuery query) {
    return queryRepository.findPage(query);
  }

  @Override
  public long countPage(ProductPageQuery query) {
    return queryRepository.countPage(query);
  }

  @Override
  public Optional<Product> findById(Long id) {
    return queryRepository.findById(id);
  }
}
