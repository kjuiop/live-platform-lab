package org.giglab.live.commerce.api.controller.exception;

import org.giglab.live.commerce.core.global.exception.DomainErrorCode;
import org.springframework.http.HttpStatus;

public class DomainHttpStatusResolver {

  private DomainHttpStatusResolver() {}

  public static HttpStatus resolve(DomainErrorCode errorCode) {
    String code = errorCode.getCode(); // e.g. "MEMBER-4001"
    int hyphenIndex = code.indexOf('-');
    if (hyphenIndex < 0 || hyphenIndex == code.length() - 1) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    int numeric;
    try {
      numeric = Integer.parseInt(code.substring(hyphenIndex + 1));
    } catch (NumberFormatException e) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // 4xxx: 클라이언트 오류
    if (numeric >= 4000 && numeric < 4100) {
      // 일반적인 잘못된 요청 (validation, 상태 오류 등)
      return HttpStatus.BAD_REQUEST;
    }
    if (numeric >= 4100 && numeric < 4200) {
      // 인증 관련 오류 (UNAUTHORIZED_USER 등)
      return HttpStatus.UNAUTHORIZED;
    }
    if (numeric >= 4300 && numeric < 4400) {
      // 권한 부족 (FORBIDDEN 등)
      return HttpStatus.FORBIDDEN;
    }
    if (numeric >= 4400 && numeric < 4500) {
      // 리소스를 찾을 수 없음
      return HttpStatus.NOT_FOUND;
    }

    // 5xxx: 서버/비즈니스 처리 중 서버 오류로 볼 수 있는 케이스
    if (numeric >= 5000 && numeric < 6000) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // 규칙 밖은 일단 서버 오류로 간주
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
