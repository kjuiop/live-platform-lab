package org.giglab.live.commerce.core.product.application.dto;

import java.math.BigDecimal;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record ProductSummary(
    Long id, String name, ProductStatusType status, BigDecimal price, int stockQuantity) {

  public ProductSummary(
      Long id, String name, ProductStatusType status, BigDecimal price, int stockQuantity) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.price = price;
    this.stockQuantity = stockQuantity;
  }
}
