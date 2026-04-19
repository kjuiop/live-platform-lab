package org.giglab.live.commerce.api.dto.product;

import java.util.List;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;

public record GetProductListResponse(
    List<ProductSummary> items, Long nextCursor, boolean hasNext) {}
