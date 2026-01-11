package org.giglab.live.infrastructure.redis.exception;

import lombok.Getter;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
public class RedisOperationException extends RuntimeException {

  private final String operation;
  private final String key;

  public RedisOperationException(String operation, String key, String message, Throwable cause) {
    super(String.format("Redis %s operation failed for key '%s': %s", operation, key, message), cause);
    this.operation = operation;
    this.key = key;
  }
}
