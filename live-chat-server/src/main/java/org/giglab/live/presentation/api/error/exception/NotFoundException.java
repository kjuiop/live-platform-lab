package org.giglab.live.presentation.api.error.exception;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
