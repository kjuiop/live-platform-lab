package org.giglab.live.commerce.api.dto.product;

public record ParsedProductResponse(
    Long documentId,
    String name,
    Integer price,
    String description,
    String manufacturer,
    String ingredients,
    String usageMethod) {}
