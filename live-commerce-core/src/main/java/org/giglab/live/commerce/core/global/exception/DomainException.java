package org.giglab.live.commerce.core.global.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

  private final DomainErrorCode errorCode;

  protected DomainException(DomainErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  protected DomainException(DomainErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  protected DomainException(DomainErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}
