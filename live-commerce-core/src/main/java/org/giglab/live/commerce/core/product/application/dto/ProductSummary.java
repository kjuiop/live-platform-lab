package org.giglab.live.commerce.core.product.application.dto;

import java.math.BigDecimal;
import org.giglab.live.commerce.core.product.domain.entity.types.EmbeddingStatusType;
import org.giglab.live.commerce.core.product.domain.entity.types.ProductStatusType;

public record ProductSummary(
    Long id,
    String name,
    ProductStatusType status,
    BigDecimal price,
    int stockQuantity,
    String categoryName,
    EmbeddingStatusType embeddingStatus) {}
