package org.giglab.live.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum ActionErrorCode implements DomainErrorCode {
  UNSUPPORTED_ACTION("ACTION-4001", "지원하지 않는 액션입니다.");

  private final String code;
  private final String message;
}
