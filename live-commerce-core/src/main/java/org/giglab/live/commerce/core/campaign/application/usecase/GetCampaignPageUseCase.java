package org.giglab.live.commerce.core.campaign.application.usecase;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignPageQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignPageResult;
import org.giglab.live.commerce.core.campaign.application.port.persistence.CampaignQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCampaignPageUseCase {

  private final CampaignQueryPort campaignQueryPort;

  public GetCampaignPageResult execute(CampaignPageQuery query) {
    List<CampaignSummary> items = campaignQueryPort.findPage(query);
    long totalCount = campaignQueryPort.countPage(query);
    return GetCampaignPageResult.of(items, query.page(), query.size(), totalCount);
  }
}
