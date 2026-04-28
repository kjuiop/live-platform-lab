package org.giglab.live.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements DomainErrorCode {
  NOT_FOUND("ROOM-4401", "존재하지 않는 방송입니다."),
  INVALID_TITLE("ROOM-4001", "방송 제목이 유효하지 않습니다.");

  private final String code;
  private final String message;
}
