package org.giglab.live.commerce.core.product.application.dto.pdf;

public record ParsedPdfData(String filename, String extractedText, LlmParsedFields fields) {}
