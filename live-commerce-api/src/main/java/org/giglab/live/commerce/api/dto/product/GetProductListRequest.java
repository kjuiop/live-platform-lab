package org.giglab.live.commerce.api.dto.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.giglab.live.commerce.api.dto.pagination.CursorPagination;
import org.giglab.live.commerce.api.dto.pagination.OffsetPagination;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record GetProductListRequest(
    Long cursor,
    Integer page,
    @Min(1) @Max(100) Integer size,
    String keyword,
    ProductStatusType status)
    implements CursorPagination, OffsetPagination {

  @Override
  public int sizeOrDefault() {
    return size != null ? size : 20;
  }

  public boolean isOffsetMode() {
    return page != null;
  }
}
