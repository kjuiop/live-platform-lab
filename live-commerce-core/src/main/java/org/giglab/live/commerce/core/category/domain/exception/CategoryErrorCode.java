package org.giglab.live.commerce.core.category.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements DomainErrorCode {
  INVALID_CATEGORY("CATEGORY-4001", "유효하지 않은 카테고리 ID입니다."),
  NOT_FOUND("CATEGORY-4401", "존재하지 않는 카테고리입니다.");

  private final String code;
  private final String message;
}
