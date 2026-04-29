package org.giglab.live.presentation.api.error;

import org.giglab.live.global.exception.DomainErrorCode;
import org.springframework.http.HttpStatus;

public class DomainHttpStatusResolver {

  private DomainHttpStatusResolver() {}

  public static HttpStatus resolve(DomainErrorCode errorCode) {
    String code = errorCode.getCode(); // e.g. "ROOM-4401"
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

    if (numeric >= 4000 && numeric < 4100) {
      return HttpStatus.BAD_REQUEST;
    }
    if (numeric >= 4100 && numeric < 4200) {
      return HttpStatus.UNAUTHORIZED;
    }
    if (numeric >= 4200 && numeric < 4300) {
      return HttpStatus.CONFLICT;
    }
    if (numeric >= 4300 && numeric < 4400) {
      return HttpStatus.FORBIDDEN;
    }
    if (numeric >= 4400 && numeric < 4500) {
      return HttpStatus.NOT_FOUND;
    }
    if (numeric >= 5000 && numeric < 6000) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
