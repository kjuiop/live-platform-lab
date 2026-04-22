package org.giglab.live.commerce.core.product.application.dto.pdf;

public record ParsedProductResult(
    String name,
    Integer price,
    String description,
    String manufacturer,
    String ingredients,
    String usageMethod,
    // PDF에서 추출된 원본 텍스트, 클라이언트가 임시 보관 -> 상품 저장 시 함께 전송
    String extractedText) {}
