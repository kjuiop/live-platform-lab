package org.giglab.live.commerce.api.dto.product;

import java.math.BigDecimal;

public record ProductSummaryItem(
    Long id,
    String name,
    String status,
    BigDecimal price,
    int stockQuantity,
    String categoryName) {}
