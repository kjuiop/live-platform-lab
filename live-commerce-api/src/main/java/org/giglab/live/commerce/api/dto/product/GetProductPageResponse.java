package org.giglab.live.commerce.api.dto.product;

import java.util.List;

public record GetProductPageResponse(
    List<ProductSummaryItem> items, int page, int totalPages, long totalCount, boolean hasNext) {}
