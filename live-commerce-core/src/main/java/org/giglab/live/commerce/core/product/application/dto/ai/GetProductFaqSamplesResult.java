package org.giglab.live.commerce.core.product.application.dto.ai;

import java.util.List;

public record GetProductFaqSamplesResult(Long productId, List<ProductFaqSampleResult> items) {}
