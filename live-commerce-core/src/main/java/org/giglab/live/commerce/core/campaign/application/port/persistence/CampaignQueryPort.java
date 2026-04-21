package org.giglab.live.commerce.core.campaign.application.port.persistence;

import java.util.List;
import java.util.Optional;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;
import org.giglab.live.commerce.core.campaign.application.dto.GetCampaignResult;

public interface CampaignQueryPort {
  List<CampaignSummary> findList(CampaignListQuery query);

  Optional<GetCampaignResult> findById(Long campaignId);
}
