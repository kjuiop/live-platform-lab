package org.giglab.live.commerce.core.product.application.dto.ai;

import java.util.Map;

public record PdfDocumentMetadata(long productId, long documentId, String filename) {

  public Map<String, Object> toMap() {
    return Map.of(
        "productId", productId,
        "documentId", documentId,
        "type", "pdf",
        "filename", filename);
  }
}
