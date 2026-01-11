package org.giglab.live.presentation.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
  private HttpStatus status;
  private Object data;
  private String message;
  private String errorCode;

  public ApiResponse(HttpStatus status, Object data) {
    this.status = status;
    this.data = data;
  }

  public ApiResponse(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public static ApiResponse OK() {
    return new ApiResponse(HttpStatus.OK, null);
  }

  public static ApiResponse OK(Object data) {
    return new ApiResponse(HttpStatus.OK, data);
  }

  public static ApiResponse ERROR(HttpStatus status, Object data, String message, String errorCode) {
    return new ApiResponse(status, data, message, errorCode);
  }

  public static ApiResponse ERROR(HttpStatus status, String message) {
    return new ApiResponse(status, message);
  }
}
