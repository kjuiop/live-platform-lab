package org.giglab.live.domain.exception;

import org.giglab.live.global.exception.DomainException;

public class RoomDomainException extends DomainException {

  public RoomDomainException(RoomErrorCode errorCode) {
    super(errorCode);
  }

  public RoomDomainException(RoomErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public RoomDomainException(RoomErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
