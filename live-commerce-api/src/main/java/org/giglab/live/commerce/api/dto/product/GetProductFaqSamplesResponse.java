package org.giglab.live.commerce.api.dto.product;

import java.util.List;

public record GetProductFaqSamplesResponse(Long productId, List<ProductFaqSampleItem> items) {}
