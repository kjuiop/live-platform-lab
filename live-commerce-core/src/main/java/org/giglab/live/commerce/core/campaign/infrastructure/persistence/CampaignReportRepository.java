package org.giglab.live.commerce.core.campaign.infrastructure.persistence;

import java.util.Optional;
import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignReportRepository extends JpaRepository<CampaignReport, Long> {
  Optional<CampaignReport> findByRoomId(String roomId);

  Optional<CampaignReport> findByCampaignId(Long campaignId);
}
