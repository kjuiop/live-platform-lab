package org.giglab.live.commerce.core.campaign.infrastructure.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.giglab.live.commerce.core.campaign.infrastructure.persistence.CampaignQueryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaCampaignQueryAdapter implements CampaignQueryPort {

  private final CampaignQueryRepository queryRepository;

  @Override
  public List<CampaignSummary> findList(CampaignListQuery query) {
    return queryRepository.findList(query);
  }
}
