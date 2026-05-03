package org.giglab.live.analytics.api.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.AnalyticsService;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.presentation.dto.FunnelResponse;
import org.giglab.live.analytics.api.presentation.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/rooms/{roomId}")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/funnel")
  public ApiResponse<FunnelResponse> getFunnel(@PathVariable String roomId) {
    GetFunnelResult result = analyticsService.getFunnel(roomId);
    FunnelResponse response = FunnelResponse.from(result);
    return ApiResponse.success(response);
  }
}
