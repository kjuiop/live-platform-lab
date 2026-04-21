package org.giglab.live.commerce.core.product.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements DomainErrorCode {
  PRODUCT_NOT_FOUND("PRODUCT-4401", "존재하지 않는 상품입니다.");

  private final String code;
  private final String message;
}
