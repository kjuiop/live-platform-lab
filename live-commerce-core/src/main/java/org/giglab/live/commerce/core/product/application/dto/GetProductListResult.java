package org.giglab.live.commerce.core.product.application.dto;

import java.util.List;

public record GetProductListResult(List<ProductSummary> items, Long nextCursor, boolean hasNext) {

  public static GetProductListResult of(List<ProductSummary> fetched, int requestedSize) {
    boolean hasNext = fetched.size() > requestedSize;
    List<ProductSummary> items = hasNext ? fetched.subList(0, requestedSize) : fetched;
    Long nextCursor = hasNext ? items.getLast().id() : null;
    return new GetProductListResult(items, nextCursor, hasNext);
  }
}
