package org.giglab.live.commerce.api.dto.product;

import java.util.List;

public record GetProductListResponse(
    List<ProductSummaryItem> items, Long nextCursor, boolean hasNext) {}
