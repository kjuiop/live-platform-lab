package org.giglab.live.analytics.api.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.giglab.live.analytics.api.application.AnalyticsService;
import org.giglab.live.analytics.api.application.dto.GetFunnelResult;
import org.giglab.live.analytics.api.application.dto.GetUtmResult;
import org.giglab.live.analytics.api.application.dto.GetViewerResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AnalyticsService analyticsService;

  // ─── /funnel ───────────────────────────────────────────────────────────────

  @Test
  void getFunnel_withDefaultParams_returns200AndFunnelData() throws Exception {
    given(analyticsService.getFunnel(eq("room-001"), any(), any()))
        .willReturn(new GetFunnelResult(100L, 30L, 10L, 3L, 3.0));

    mockMvc
        .perform(get("/api/v1/analytics/rooms/room-001/funnel"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.impression").value(100))
        .andExpect(jsonPath("$.data.click").value(30))
        .andExpect(jsonPath("$.data.addCart").value(10))
        .andExpect(jsonPath("$.data.purchase").value(3))
        .andExpect(jsonPath("$.data.cvrPct").value(3.0));

    verify(analyticsService)
        .getFunnel(eq("room-001"), any(LocalDateTime.class), any(LocalDateTime.class));
  }

  @Test
  void getFunnel_withExplicitParams_passesCorrectLocalDateTimeToService() throws Exception {
    given(analyticsService.getFunnel(any(), any(), any()))
        .willReturn(new GetFunnelResult(50L, 15L, 5L, 1L, 2.0));

    mockMvc
        .perform(
            get("/api/v1/analytics/rooms/room-001/funnel")
                .param("startAt", "2026-01-01T00:00:00")
                .param("endAt", "2026-01-31T23:59:59"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.impression").value(50));

    verify(analyticsService)
        .getFunnel(
            eq("room-001"),
            eq(LocalDateTime.of(2026, 1, 1, 0, 0, 0)),
            eq(LocalDateTime.of(2026, 1, 31, 23, 59, 59)));
  }

  // ─── /viewers ──────────────────────────────────────────────────────────────

  @Test
  void getViewers_withDefaultParams_returns200AndTotalViewers() throws Exception {
    given(analyticsService.getViewer(eq("room-001"), any(), any()))
        .willReturn(new GetViewerResult(80L));

    mockMvc
        .perform(get("/api/v1/analytics/rooms/room-001/viewers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalViewers").value(80));

    verify(analyticsService)
        .getViewer(eq("room-001"), any(LocalDateTime.class), any(LocalDateTime.class));
  }

  @Test
  void getViewers_withNoData_returns200AndZero() throws Exception {
    given(analyticsService.getViewer(eq("room-999"), any(), any()))
        .willReturn(new GetViewerResult(0L));

    mockMvc
        .perform(get("/api/v1/analytics/rooms/room-999/viewers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalViewers").value(0));
  }

  // ─── /utm ──────────────────────────────────────────────────────────────────

  @Test
  void getUtm_withDefaultParams_returns200AndUtmList() throws Exception {
    given(analyticsService.getUtm(eq("room-001"), any(), any()))
        .willReturn(
            List.of(
                new GetUtmResult("google", 50L, 5L, 10.0),
                new GetUtmResult("facebook", 30L, 2L, 6.67)));

    mockMvc
        .perform(get("/api/v1/analytics/rooms/room-001/utm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].utmSource").value("google"))
        .andExpect(jsonPath("$.data[0].visitors").value(50))
        .andExpect(jsonPath("$.data[0].purchases").value(5))
        .andExpect(jsonPath("$.data[0].cvrPct").value(10.0))
        .andExpect(jsonPath("$.data[1].utmSource").value("facebook"))
        .andExpect(jsonPath("$.data[1].visitors").value(30));

    verify(analyticsService)
        .getUtm(eq("room-001"), any(LocalDateTime.class), any(LocalDateTime.class));
  }

  @Test
  void getUtm_withNoData_returns200AndEmptyList() throws Exception {
    given(analyticsService.getUtm(eq("room-999"), any(), any())).willReturn(List.of());

    mockMvc
        .perform(get("/api/v1/analytics/rooms/room-999/utm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isEmpty());
  }
}
