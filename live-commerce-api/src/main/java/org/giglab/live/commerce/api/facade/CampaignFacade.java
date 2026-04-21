package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignRequest;
import org.giglab.live.commerce.api.dto.campaign.CreateCampaignResponse;
import org.giglab.live.commerce.api.mapper.campaign.CampaignMapper;
import org.giglab.live.commerce.core.campaign.application.CampaignService;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignFacade {

  private final CampaignMapper campaignMapper;
  private final CampaignService campaignService;

  public CreateCampaignResponse create(CreateCampaignRequest request) {
    CreateCampaignCommand command = campaignMapper.toCreateCampaignCommand(request);
    CreateCampaignResult result = campaignService.create(command);
    return campaignMapper.toCreateCampaignResponse(result);
  }
}
