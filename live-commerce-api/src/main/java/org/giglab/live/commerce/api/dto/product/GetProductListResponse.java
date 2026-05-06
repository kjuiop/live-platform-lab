package org.giglab.live.commerce.api.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetProductListResponse(
    List<ProductSummaryItem> items,
    Long nextCursor,
    Boolean hasNext,
    Integer page,
    Integer totalPages,
    Long totalCount) {

  public static GetProductListResponse ofCursor(
      List<ProductSummaryItem> items, Long nextCursor, boolean hasNext) {
    return new GetProductListResponse(items, nextCursor, hasNext, null, null, null);
  }

  public static GetProductListResponse ofOffset(
      List<ProductSummaryItem> items, int page, int totalPages, long totalCount) {
    boolean hasNext = page < totalPages;
    return new GetProductListResponse(items, null, hasNext, page, totalPages, totalCount);
  }
}
