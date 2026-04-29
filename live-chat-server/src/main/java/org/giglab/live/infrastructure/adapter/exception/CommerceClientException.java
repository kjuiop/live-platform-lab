package org.giglab.live.infrastructure.adapter.exception;

public class CommerceClientException extends RuntimeException {

  public CommerceClientException(String message) {
    super(message);
  }

  public CommerceClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
