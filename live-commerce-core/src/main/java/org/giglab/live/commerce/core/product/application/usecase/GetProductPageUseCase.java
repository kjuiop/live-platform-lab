package org.giglab.live.commerce.core.product.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.GetProductPageResult;
import org.giglab.live.commerce.core.product.application.dto.ProductPageQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductPageUseCase {

  private final ProductQueryPort productQueryPort;

  public GetProductPageResult execute(ProductPageQuery query) {
    List<ProductSummary> items = productQueryPort.findPage(query);
    long totalCount = productQueryPort.countPage(query);
    return GetProductPageResult.of(items, query.page(), query.size(), totalCount);
  }
}
