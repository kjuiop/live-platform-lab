package org.giglab.live.commerce.core.campaign.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.infrastructure.persistence.CampaignRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCampaignStoreAdapter implements CampaignStorePort {

  private final CampaignRepository campaignRepository;

  @Override
  public Campaign store(Campaign newCampaign) {
    return campaignRepository.save(newCampaign);
  }
}
