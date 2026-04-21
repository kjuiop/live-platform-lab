package org.giglab.live.commerce.core.campaign.application.usecase;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignProductDto;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignCommand;
import org.giglab.live.commerce.core.campaign.application.dto.CreateCampaignResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateCampaignUseCase {

  private final CampaignStorePort campaignStorePort;

  public CreateCampaignResult execute(CreateCampaignCommand command) {

    Campaign campaign =
        Campaign.create(command.title(), command.description(), command.scheduledAt());

    for (CampaignProductDto product : command.campaignProducts()) {
      campaign.addProduct(product.productId(), product.displayOrder());
    }

    Campaign savedCampaign = campaignStorePort.store(campaign);
    return new CreateCampaignResult(savedCampaign.getId());
  }
}
