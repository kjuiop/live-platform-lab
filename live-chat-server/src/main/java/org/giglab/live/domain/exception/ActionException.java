package org.giglab.live.domain.exception;

import org.giglab.live.global.exception.DomainException;

public class ActionException extends DomainException {

  public ActionException(ActionErrorCode errorCode) {
    super(errorCode);
  }

  public ActionException(ActionErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
