package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.List;
import java.util.Optional;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductPageQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.domain.entity.Product;

public interface ProductQueryPort {
  List<ProductSummary> findList(ProductListQuery query);

  List<ProductSummary> findPage(ProductPageQuery query);

  long countPage(ProductPageQuery query);

  Optional<Product> findById(Long id);
}
