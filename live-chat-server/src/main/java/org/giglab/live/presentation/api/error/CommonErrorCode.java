package org.giglab.live.presentation.api.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode {
  INVALID_REQUEST("COMMON-4001", "잘못된 요청입니다."),
  UNEXPECTED_ERROR("COMMON-5001", "서버 내부 오류가 발생했습니다.");

  private final String code;
  private final String message;
}
