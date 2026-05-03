package org.giglab.live.analytics.api.presentation.dto;

import org.giglab.live.analytics.api.application.dto.GetFunnelResult;

public record FunnelResponse(
    long impression, long click, long addCart, long purchase, double cvrPct) {

  public static FunnelResponse from(GetFunnelResult result) {
    return new FunnelResponse(
        result.impression(), result.click(), result.addCart(), result.purchase(), result.cvrPct());
  }
}
