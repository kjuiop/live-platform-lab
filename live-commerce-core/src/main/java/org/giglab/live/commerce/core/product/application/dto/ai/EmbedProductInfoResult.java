package org.giglab.live.commerce.core.product.application.dto.ai;

import org.giglab.live.commerce.core.product.domain.entity.types.EmbeddingStatusType;

public record EmbedProductInfoResult(Long productId, EmbeddingStatusType embeddingStatus) {}
