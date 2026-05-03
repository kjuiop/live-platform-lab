package org.giglab.live.analytics.api.presentation.controller;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.analytics.api.application.AnalyticsService;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;
import org.giglab.live.analytics.api.presentation.dto.FunnelResponse;
import org.giglab.live.analytics.api.presentation.dto.UtmResponse;
import org.giglab.live.analytics.api.presentation.dto.ViewerResponse;
import org.giglab.live.analytics.api.presentation.response.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/rooms/{roomId}")
@RequiredArgsConstructor
public class AnalyticsController {

  private final AnalyticsService analyticsService;

  @GetMapping("/funnel")
  public ApiResponse<FunnelResponse> getFunnel(
      @PathVariable String roomId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime startAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime endAt) {
    LocalDateTime resolvedStart = startAt != null ? startAt : LocalDateTime.now().minusDays(30);
    LocalDateTime resolvedEnd = endAt != null ? endAt : LocalDateTime.now();
    GetFunnelResult result = analyticsService.getFunnel(roomId, resolvedStart, resolvedEnd);
    FunnelResponse response = FunnelResponse.from(result);
    return ApiResponse.success(response);
  }

  @GetMapping("/viewers")
  public ApiResponse<ViewerResponse> getViewers(
      @PathVariable String roomId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime startAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime endAt) {
    LocalDateTime resolvedStart = startAt != null ? startAt : LocalDateTime.now().minusDays(30);
    LocalDateTime resolvedEnd = endAt != null ? endAt : LocalDateTime.now();
    GetViewerResult result = analyticsService.getViewer(roomId, resolvedStart, resolvedEnd);
    ViewerResponse response = ViewerResponse.from(result);
    return ApiResponse.success(response);
  }

  @GetMapping("/utm")
  public ApiResponse<List<UtmResponse>> getUtm(
      @PathVariable String roomId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime startAt,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime endAt) {
    LocalDateTime resolvedStart = startAt != null ? startAt : LocalDateTime.now().minusDays(30);
    LocalDateTime resolvedEnd = endAt != null ? endAt : LocalDateTime.now();
    List<GetUtmResult> results = analyticsService.getUtm(roomId, resolvedStart, resolvedEnd);
    List<UtmResponse> response = results.stream().map(UtmResponse::from).toList();
    return ApiResponse.success(response);
  }
}
