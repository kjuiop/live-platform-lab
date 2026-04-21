package org.giglab.live.commerce.core.campaign.application.port.persistence;

import org.giglab.live.commerce.core.campaign.domain.entity.Campaign;

public interface CampaignStorePort {
  Campaign store(Campaign newCampaign);
}
