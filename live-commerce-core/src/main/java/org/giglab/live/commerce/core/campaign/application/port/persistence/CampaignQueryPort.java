package org.giglab.live.commerce.core.campaign.application.port.persistence;

import java.util.List;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignListQuery;
import org.giglab.live.commerce.core.campaign.application.dto.CampaignSummary;

public interface CampaignQueryPort {
  List<CampaignSummary> findList(CampaignListQuery query);
}
