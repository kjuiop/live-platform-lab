package org.giglab.live.analytics.api.presentation.dto;

import org.giglab.live.analytics.api.application.dto.GetUtmResult;

public record UtmResponse(String utmSource, long visitors, long purchases, double cvrPct) {

  public static UtmResponse from(GetUtmResult result) {
    return new UtmResponse(
        result.utmSource(), result.visitors(), result.purchases(), result.cvrPct());
  }
}
