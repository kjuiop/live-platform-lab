package org.giglab.live.domain.exception;

import org.giglab.live.global.exception.DomainException;

public class BannerDomainException extends DomainException {

  public BannerDomainException(BannerErrorCode errorCode) {
    super(errorCode);
  }
}
