package org.giglab.live.commerce.core.campaign.infrastructure.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;
import org.giglab.live.commerce.core.campaign.infrastructure.persistence.CampaignRepository;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCampaignStoreAdapter implements CampaignStorePort {

  private final CampaignRepository campaignRepository;

  @Override
  public Campaign store(Campaign newCampaign) {
    return campaignRepository.save(newCampaign);
  }

  @Override
  public Optional<Campaign> findEntityById(Long campaignId) {
    return campaignRepository.findByIdAndDeleteYn(campaignId, YnType.N);
  }

  @Override
  public Optional<Campaign> findEntityByChatRoomId(String chatRoomId) {
    return campaignRepository.findByChatRoomId(chatRoomId);
  }
}
