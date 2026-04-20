package org.giglab.live.commerce.api.dto.product;

import java.math.BigDecimal;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record ProductSummaryItem(
    Long id,
    String name,
    ProductStatusType status,
    BigDecimal price,
    int stockQuantity,
    String categoryName) {}
