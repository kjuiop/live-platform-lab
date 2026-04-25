package org.giglab.live.commerce.api.controller.broadcast;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.broadcast.SaveCampaignReportRequest;
import org.giglab.live.commerce.api.facade.CampaignReportFacade;
import org.giglab.live.commerce.api.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CampaignReport", description = "방송 리포트 API")
@RestController
@RequestMapping("/api/v1/campaign-reports")
@RequiredArgsConstructor
public class CampaignReportController {

  private final CampaignReportFacade campaignReportFacade;

  @Operation(summary = "방송 리포트 저장", description = "방송 종료 후 AI 분석 리포트를 저장합니다.")
  @PostMapping
  public ApiResponse<Void> save(@RequestBody @Valid SaveCampaignReportRequest request) {
    campaignReportFacade.save(request);
    return ApiResponse.success();
  }
}
