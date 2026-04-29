package org.giglab.live.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum FaqErrorCode implements DomainErrorCode {
  EMPTY_QUESTION("FAQ-4001", "질문은 비어있을 수 없습니다."),
  INVALID_PRODUCT_ID("FAQ-4002", "productId가 올바르지 않습니다.");

  private final String code;
  private final String message;
}
