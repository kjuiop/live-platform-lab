package org.giglab.live.presentation.api.error;

import lombok.Getter;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
