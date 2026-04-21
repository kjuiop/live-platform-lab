package org.giglab.live.commerce.core.campaign.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.usecase.CreateCampaignUseCase;
import org.giglab.live.commerce.core.campaign.application.usecase.GetCampaignListUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {

  private final GetCampaignListUseCase getCampaignListUseCase;
  private final CreateCampaignUseCase createCampaignUseCase;

  public GetCampaignListResult getList(CampaignListQuery query) {
    return getCampaignListUseCase.execute(query);
  }

  public CreateCampaignResult create(CreateCampaignCommand request) {
    return createCampaignUseCase.execute(request);
  }
}
