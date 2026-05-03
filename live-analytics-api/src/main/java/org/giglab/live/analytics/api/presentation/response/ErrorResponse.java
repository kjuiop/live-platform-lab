package org.giglab.live.analytics.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
  private String code;
  private String message;
}
