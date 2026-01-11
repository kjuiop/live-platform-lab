package org.giglab.live.presentation.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

  // 입력값 검증 에러 (4000번대)
  INVALID_INPUT_VALUE("4001", "잘못된 입력값입니다."),
  // 데이터 없음 (4000번대)
  DATA_NOT_FOUND("4004", "데이터를 찾을 수 없습니다."),
  // 서버 에러 (5000번대)
  INTERNAL_SERVER_ERROR("5001", "서버 내부 오류가 발생했습니다.");

  private final String code;
  private final String message;
}
