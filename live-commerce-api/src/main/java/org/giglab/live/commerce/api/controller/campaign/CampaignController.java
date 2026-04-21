package org.giglab.live.commerce.api.controller.campaign;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListRequest;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignResponse;
import org.giglab.live.commerce.api.facade.CampaignFacade;
import org.giglab.live.commerce.api.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Campaign", description = "캠페인 API")
@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {

  private final CampaignFacade campaignFacade;

  @Operation(summary = "캠페인 목록 조회", description = "커서 기반 페이지네이션으로 캠페인 목록을 조회합니다.")
  @GetMapping
  public ApiResponse<GetCampaignListResponse> getList(@Valid GetCampaignListRequest request) {
    return ApiResponse.success(campaignFacade.getList(request));
  }

  @Operation(summary = "캠페인 상세 조회", description = "캠페인 ID로 캠페인 상세 정보를 조회합니다.")
  @GetMapping("/{campaignId}")
  public ApiResponse<GetCampaignResponse> getDetail(@PathVariable Long campaignId) {
    return ApiResponse.success(campaignFacade.getDetail(campaignId));
  }

  @Operation(summary = "캠페인 등록", description = "캠페인을 등록합니다.")
  @PostMapping
  public ApiResponse<CreateCampaignResponse> create(
      @RequestBody @Valid CreateCampaignRequest request) {
    return ApiResponse.success(campaignFacade.create(request));
  }
}
