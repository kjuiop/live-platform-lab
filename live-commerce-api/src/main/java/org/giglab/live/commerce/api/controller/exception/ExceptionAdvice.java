package org.giglab.live.commerce.api.controller.exception;

import static org.giglab.live.commerce.api.controller.exception.CommonErrorCode.INVALID_REQUEST_ERROR;
import static org.giglab.live.commerce.api.controller.exception.CommonErrorCode.UNEXPECTED_ERROR;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.giglab.live.commerce.api.response.ApiResponse;
import org.giglab.live.commerce.core.global.exception.DomainErrorCode;
import org.giglab.live.commerce.core.global.exception.DomainException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
    HttpStatus status = ex.getStatus();
    logByStatus(status, "ApiException", ex.getCode(), ex.getMessage(), ex);
    return ResponseEntity.status(status).body(ApiResponse.error(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
    DomainErrorCode errorCode = ex.getErrorCode();
    HttpStatus status = DomainHttpStatusResolver.resolve(errorCode);
    String detailErrorMessage = ex.getMessage();
    logByStatus(status, "DomainException", errorCode.getCode(), detailErrorMessage, ex);
    return ResponseEntity.status(status)
        .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse(INVALID_REQUEST_ERROR.getMessage());

    log.warn("MethodArgumentNotValid: {}", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(INVALID_REQUEST_ERROR.getCode(), message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
      ConstraintViolationException ex) {
    String message =
        ex.getConstraintViolations().stream()
            .findFirst()
            .map(ConstraintViolation::getMessage)
            .orElse(INVALID_REQUEST_ERROR.getMessage());

    log.warn("ConstraintViolation: {}", message);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(INVALID_REQUEST_ERROR.getCode(), message));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(INVALID_REQUEST_ERROR.getCode(), INVALID_REQUEST_ERROR.getMessage()));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Void>> handleMissingRequestParameter(
      MissingServletRequestParameterException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(INVALID_REQUEST_ERROR.getCode(), INVALID_REQUEST_ERROR.getMessage()));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ApiResponse<Void>> handleMissingRequestHeader(
      MissingRequestHeaderException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(INVALID_REQUEST_ERROR.getCode(), INVALID_REQUEST_ERROR.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(INVALID_REQUEST_ERROR.getCode(), INVALID_REQUEST_ERROR.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
    log.error("Unexpected exception occurred.", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(UNEXPECTED_ERROR.getCode(), UNEXPECTED_ERROR.getMessage()));
  }

  private void logByStatus(
      HttpStatus status, String marker, String code, String detailErrorMessage, Throwable ex) {
    if (status.is4xxClientError()) {
      log.warn("{} client error, code={}, message={}", marker, code, detailErrorMessage);
    } else if (status.is5xxServerError()) {
      log.error("{} server error, code={}, message={}", marker, code, detailErrorMessage, ex);
    } else {
      log.error(
          "{} (unexpected status). code={}, message={}, status={}",
          marker,
          code,
          detailErrorMessage,
          status,
          ex);
    }
  }
}
