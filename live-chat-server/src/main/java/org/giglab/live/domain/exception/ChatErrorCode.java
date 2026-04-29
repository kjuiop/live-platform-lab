package org.giglab.live.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements DomainErrorCode {
  EMPTY_MESSAGE("CHAT-4001", "메시지는 비어있을 수 없습니다.");

  private final String code;
  private final String message;
}
