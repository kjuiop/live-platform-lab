package org.giglab.live.presentation.api.error;

import lombok.extern.slf4j.Slf4j;
import org.giglab.live.presentation.api.error.exception.NotFoundException;
import org.giglab.live.presentation.api.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(NotFoundException e) {
    return ResponseEntity.badRequest()
      .body(new ErrorResponse(e.getMessage(), "E001"));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
    return ResponseEntity.badRequest()
      .body(new ErrorResponse(e.getMessage(), "E002"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
    MethodArgumentNotValidException ex) {
    BindingResult bindingResult = ex.getBindingResult();
    List<FieldError> fieldErrors = bindingResult.getFieldErrors();

    String errorMessage = fieldErrors.stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .collect(Collectors.joining(", "));

    return ResponseEntity.badRequest()
      .body(new ErrorResponse(errorMessage, "E003"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
    log.error("Unexpected error occurred: ", e);  // 로깅 추가
    return ResponseEntity.internalServerError()
      .body(new ErrorResponse("E999", e.getMessage()));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getErrorCode().getCode(), e.getMessage()));
  }
}
