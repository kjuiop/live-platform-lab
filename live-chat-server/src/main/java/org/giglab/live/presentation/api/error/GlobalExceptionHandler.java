package org.giglab.live.presentation.api.error;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.global.exception.DomainException;
import org.giglab.live.presentation.api.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
    HttpStatus status = DomainHttpStatusResolver.resolve(e.getErrorCode());
    logByStatus(status, e.getErrorCode().getCode(), e.getMessage(), e);
    return ResponseEntity.status(status)
        .body(new ErrorResponse(e.getErrorCode().getCode(), e.getErrorCode().getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    String errorMessage =
        fieldErrors.stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return ResponseEntity.badRequest()
        .body(new ErrorResponse(CommonErrorCode.INVALID_REQUEST.getCode(), errorMessage));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
    log.error("Unexpected error occurred: ", e);
    return ResponseEntity.internalServerError()
        .body(
            new ErrorResponse(
                CommonErrorCode.UNEXPECTED_ERROR.getCode(),
                CommonErrorCode.UNEXPECTED_ERROR.getMessage()));
  }

  private void logByStatus(HttpStatus status, String code, String message, Throwable ex) {
    if (status.is4xxClientError()) {
      log.warn("DomainException client error, code={}, message={}", code, message);
    } else {
      log.error("DomainException server error, code={}, message={}", code, message, ex);
    }
  }
}
