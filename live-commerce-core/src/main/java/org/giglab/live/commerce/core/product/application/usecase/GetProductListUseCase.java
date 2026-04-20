package org.giglab.live.commerce.core.product.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductListUseCase {

  private final ProductQueryPort productQueryPort;

  public GetProductListResult execute(ProductListQuery query) {
    List<ProductSummary> fetched = productQueryPort.findList(query);
    return GetProductListResult.of(fetched, query.size());
  }
}
