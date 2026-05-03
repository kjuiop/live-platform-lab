package org.giglab.live.analytics.api.presentation.dto;

import org.giglab.live.analytics.api.application.dto.GetViewerResult;

public record ViewerResponse(long totalViewers) {

  public static ViewerResponse from(GetViewerResult result) {
    return new ViewerResponse(result.totalViewers());
  }
}
