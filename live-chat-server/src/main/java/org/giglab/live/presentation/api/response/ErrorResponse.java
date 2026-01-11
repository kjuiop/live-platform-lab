package org.giglab.live.presentation.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : JAKE
 * @date : 26. 1. 11.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
  private String code;
  private String message;
}
