package org.giglab.live.commerce.core.product.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull BigDecimal price,
    @Size(min = 1) @NotNull List<Long> categoryIds,
    int stockQuantity,
    int sortOrder,
    String manufacturer,
    String ingredients,
    String usageMethod) {}
