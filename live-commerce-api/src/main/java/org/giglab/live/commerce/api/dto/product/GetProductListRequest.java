package org.giglab.live.commerce.api.dto.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetProductListRequest(Long cursor, @Min(1) @Max(100) int size) {}
