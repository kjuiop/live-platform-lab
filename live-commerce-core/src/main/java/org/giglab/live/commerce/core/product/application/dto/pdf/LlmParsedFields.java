package org.giglab.live.commerce.core.product.application.dto.pdf;

public record LlmParsedFields(
    String name,
    Integer price,
    String description,
    String manufacturer,
    String ingredients,
    String usageMethod) {}
