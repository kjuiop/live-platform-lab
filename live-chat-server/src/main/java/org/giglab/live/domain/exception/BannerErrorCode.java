package org.giglab.live.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum BannerErrorCode implements DomainErrorCode {
  INVALID_PRODUCT_ID("BANNER-4001", "productId가 올바르지 않습니다.");

  private final String code;
  private final String message;
}
