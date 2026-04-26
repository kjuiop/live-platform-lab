package org.giglab.live.commerce.core.campaign.application.port.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;

public interface CampaignReportStorePort {
  CampaignReport store(CampaignReport campaignReport);

  Optional<CampaignReport> findByRoomId(String roomId);

  Optional<CampaignReport> findByCampaignId(Long campaignId);
}
