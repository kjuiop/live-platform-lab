package org.giglab.live.global.exception;

import org.springframework.http.HttpStatus;

public interface DomainErrorCode {

  String getCode();

  String getMessage();

  HttpStatus getHttpStatus();
}
