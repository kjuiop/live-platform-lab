package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignListResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCampaignListUseCase {

  private final CampaignQueryPort campaignQueryPort;

  public GetCampaignListResult execute(CampaignListQuery query) {
    List<CampaignSummary> fetched = campaignQueryPort.findList(query);
    return GetCampaignListResult.of(fetched, query.size());
  }
}
