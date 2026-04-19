package org.giglab.live.commerce.core.product.application.port.persistence;

import java.util.List;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;

public interface ProductQueryPort {
  List<ProductSummary> findList(ProductListQuery query);
}
