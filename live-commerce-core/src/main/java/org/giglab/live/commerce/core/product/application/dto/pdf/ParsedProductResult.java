package org.giglab.live.commerce.core.product.application.dto.pdf;

public record ParsedProductResult(
    Long documentId, // ProductDocument.id — 상품 저장 시 클라이언트가 전달
    String name,
    Integer price,
    String description,
    String manufacturer,
    String ingredients,
    String usageMethod) {}
