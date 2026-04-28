package org.giglab.live.commerce.core.product.application.dto.ai;

import java.util.Map;

public record ProductInfoMetadata(long productId) {

  public Map<String, Object> toMap() {
    return Map.of(
        "productId", productId,
        "type", "product_info",
        "filename", "product_info");
  }
}
