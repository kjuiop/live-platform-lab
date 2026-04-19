package org.giglab.live.commerce.core.product.entity.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatusType {
  DRAFT("Draft", "검수중"),

  ON_SALE("OnSale", "판매중"),

  OUT_OF_STOCK("OutOfStock", "재고소진"),

  HIDDEN("Hidden", "숨김");

  private final String key;

  private final String description;
}
