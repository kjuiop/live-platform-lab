package org.giglab.live.domain.exception;

import org.giglab.live.global.exception.DomainException;

public class FaqDomainException extends DomainException {

  public FaqDomainException(FaqErrorCode errorCode) {
    super(errorCode);
  }
}
