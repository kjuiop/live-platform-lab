package org.giglab.live.commerce.core.campaign.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;
import org.giglab.live.commerce.core.campaign.application.usecase.CreateCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignListUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {

  private final GetCampaignListUseCase getCampaignListUseCase;
  private final GetCampaignUseCase getCampaignUseCase;
  private final CreateCampaignUseCase createCampaignUseCase;

  public GetCampaignListResult getList(CampaignListQuery query) {
    return getCampaignListUseCase.execute(query);
  }

  public GetCampaignResult getDetail(Long campaignId) {
    return getCampaignUseCase.execute(campaignId);
  }

  public CreateCampaignResult create(CreateCampaignCommand request) {
    return createCampaignUseCase.execute(request);
  }
}
