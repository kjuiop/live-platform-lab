package org.giglab.live.commerce.api.controller.campaign;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.api.facade.CampaignFacade;
import org.giglab.live.commerce.api.response.ApiResponse;
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

  @Operation(summary = "캠페인 등록", description = "캠페인을 등록합니다.")
  @PostMapping
  public ApiResponse<CreateCampaignResponse> create(
      @RequestBody @Valid CreateCampaignRequest request) {
    return ApiResponse.success(campaignFacade.create(request));
  }
}
