package org.giglab.live.commerce.api.facade;

import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.dto.broadcast.SaveCampaignReportRequest;
import org.giglab.live.commerce.api.mapper.campaign.CampaignReportMapper;
import org.giglab.live.commerce.core.campaign.application.CampaignReportService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignReportFacade {

  private final CampaignReportService campaignReportService;
  private final CampaignReportMapper campaignReportMapper;

  public void save(SaveCampaignReportRequest request) {
    campaignReportService.save(campaignReportMapper.toSaveCampaignReportCommand(request));
  }
}
