package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListRequest;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignListResponse;
import org.giglab.live.commerce.api.dto.campaign.GetCampaignResponse;
import org.giglab.live.commerce.api.mapper.campaign.CampaignMapper;
import org.giglab.live.commerce.core.campaign.application.CampaignService;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignFacade {

  private final CampaignMapper campaignMapper;
  private final CampaignService campaignService;

  public GetCampaignListResponse getList(GetCampaignListRequest request) {
    GetCampaignListResult result =
        campaignService.getList(campaignMapper.toCampaignListQuery(request));
    return campaignMapper.toGetCampaignListResponse(result);
  }

  public GetCampaignResponse getDetail(Long campaignId) {
    GetCampaignResult result = campaignService.getDetail(campaignId);
    return campaignMapper.toGetCampaignResponse(result);
  }

  public CreateCampaignResponse create(CreateCampaignRequest request) {
    CreateCampaignCommand command = campaignMapper.toCreateCampaignCommand(request);
    CreateCampaignResult result = campaignService.create(command);
    return campaignMapper.toCreateCampaignResponse(result);
  }
}
