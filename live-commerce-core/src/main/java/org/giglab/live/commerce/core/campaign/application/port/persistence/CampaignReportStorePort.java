package org.giglab.live.commerce.core.campaign.application.port.persistence;

import org.giglab.live.commerce.core.campaign.domain.entity.CampaignReport;

public interface CampaignReportStorePort {
  CampaignReport store(CampaignReport campaignReport);
}
