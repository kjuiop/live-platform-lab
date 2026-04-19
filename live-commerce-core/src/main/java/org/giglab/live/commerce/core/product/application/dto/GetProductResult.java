package org.giglab.live.commerce.core.product.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record GetProductResult(
    Long id,
    String name,
    ProductStatusType status,
    String description,
    BigDecimal price,
    int stockQuantity,
    int sortOrder,
    String manufacturer,
    String ingredients,
    String usageMethod,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static GetProductResult from(
      Long id,
      String name,
      ProductStatusType status,
      String description,
      BigDecimal price,
      int stockQuantity,
      int sortOrder,
      String manufacturer,
      String ingredients,
      String usageMethod,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    return new GetProductResult(
        id,
        name,
        status,
        description,
        price,
        stockQuantity,
        sortOrder,
        manufacturer,
        ingredients,
        usageMethod,
        createdAt,
        updatedAt);
  }
}
