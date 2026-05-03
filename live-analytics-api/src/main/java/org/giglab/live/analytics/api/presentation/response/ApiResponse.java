package org.giglab.live.analytics.api.presentation.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

  private final T data;
  private final ErrorResponse error;

  private ApiResponse(T data, ErrorResponse error) {
    this.data = data;
    this.error = error;
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(data, null);
  }

  public static ApiResponse<Void> success() {
    return new ApiResponse<>(null, null);
  }

  public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(null, new ErrorResponse(code, message));
  }

  public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
    return new ApiResponse<>(null, errorResponse);
  }
}
