package org.giglab.live.commerce.api.controller.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode {
  INVALID_REQUEST_ERROR("COMMON-4000", "잘못된 요청입니다."),
  UNEXPECTED_ERROR("COMMON-5000", "예상치 못한 오류가 발생했습니다.");

  private final String code;
  private final String message;
}
