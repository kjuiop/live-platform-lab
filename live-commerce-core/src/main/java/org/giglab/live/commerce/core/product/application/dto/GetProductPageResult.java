package org.giglab.live.commerce.core.product.application.dto;

import java.util.List;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;

public record GetProductPageResult(
    List<ProductSummary> items,
    int page,
    int size,
    long totalCount,
    int totalPages,
    boolean hasNext) {

  public static GetProductPageResult of(
      List<ProductSummary> items, int page, int size, long totalCount) {
    if (size <= 0) {
      throw new ProductDomainException(ProductErrorCode.INVALID_PAGE_SIZE);
    }
    int totalPages = (int) Math.ceil((double) totalCount / size);
    boolean hasNext = page < totalPages;
    return new GetProductPageResult(items, page, size, totalCount, totalPages, hasNext);
  }
}
