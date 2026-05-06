package org.giglab.live.commerce.core.product.application.dto;

import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record ProductPageQuery(int page, int size, String keyword, ProductStatusType status) {

  public long offset() {
    return (long) (page - 1) * size;
  }
}
