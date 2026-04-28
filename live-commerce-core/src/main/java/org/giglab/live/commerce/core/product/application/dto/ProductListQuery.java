package org.giglab.live.commerce.core.product.application.dto;

import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record ProductListQuery(Long cursor, int size, String keyword, ProductStatusType status) {
  public int fetchSize() {
    return size + 1;
  }
}
