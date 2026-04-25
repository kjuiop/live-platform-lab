package org.giglab.live.commerce.core.campaign.infrastructure.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignReportStorePort;
import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;
import org.giglab.live.commerce.core.campaign.infrastructure.persistence.CampaignReportRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCampaignReportStoreAdapter implements CampaignReportStorePort {

  private final CampaignReportRepository campaignReportRepository;

  @Override
  public CampaignReport store(CampaignReport campaignReport) {
    return campaignReportRepository.save(campaignReport);
  }

  @Override
  public Optional<CampaignReport> findByRoomId(String roomId) {
    return campaignReportRepository.findByRoomId(roomId);
  }
}
