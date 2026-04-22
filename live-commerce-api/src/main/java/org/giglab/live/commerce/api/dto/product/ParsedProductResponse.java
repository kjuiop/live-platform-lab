package org.giglab.live.commerce.api.dto.product;

public record ParsedProductResponse(
    String name,
    Integer price,
    String description,
    String manufacturer,
    String ingredients,
    String usageMethod,
    String extractedText) {}
