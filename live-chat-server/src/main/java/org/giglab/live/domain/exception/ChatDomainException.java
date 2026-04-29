package org.giglab.live.domain.exception;

import org.giglab.live.global.exception.DomainException;

public class ChatDomainException extends DomainException {

  public ChatDomainException(ChatErrorCode errorCode) {
    super(errorCode);
  }
}
