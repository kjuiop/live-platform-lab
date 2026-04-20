package org.giglab.live.commerce.api.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetProductResponse(
    Long id,
    String name,
    String status,
    String description,
    BigDecimal price,
    int stockQuantity,
    int sortOrder,
    String manufacturer,
    String ingredients,
    String usageMethod,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
