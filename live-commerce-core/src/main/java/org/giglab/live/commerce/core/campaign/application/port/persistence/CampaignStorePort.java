package org.giglab.live.commerce.core.campaign.application.port.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;

public interface CampaignStorePort {
  Campaign store(Campaign newCampaign);

  Optional<Campaign> findEntityById(Long campaignId);

  Optional<Campaign> findEntityByChatRoomId(String chatRoomId);
}
