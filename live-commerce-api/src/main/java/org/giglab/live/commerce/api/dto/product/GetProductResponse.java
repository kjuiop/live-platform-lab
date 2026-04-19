package org.giglab.live.commerce.api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record GetProductResponse(
    Long id,
    String name,
    ProductStatusType status,
    BigDecimal price,
    int stockQuantity,
    int sortOrder,
    String manufacturer,
    String ingredients,
    String usageMethod,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
