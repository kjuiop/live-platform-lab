package org.giglab.live.commerce.core.campaign.application;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.usecase.CreateCampaignUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {

  private final CreateCampaignUseCase createCampaignUseCase;

  public CreateCampaignResult create(CreateCampaignCommand request) {
    return createCampaignUseCase.execute(request);
  }
}
