package org.giglab.live.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.giglab.live.global.exception.DomainErrorCode;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements DomainErrorCode {
  NOT_FOUND("ROOM-4401", "존재하지 않는 방송입니다."),
  INVALID_TITLE("ROOM-4001", "방송 제목이 유효하지 않습니다."),
  SEQ_FAILURE("ROOM-5001", "채팅 순서 발급에 실패했습니다.");

  private final String code;
  private final String message;
}
